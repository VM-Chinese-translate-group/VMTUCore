package top.vmctcn.vmtu.core.metadata;

import com.google.gson.Gson;
import top.vmctcn.vmtu.core.VMTUCore;
import top.vmctcn.vmtu.core.util.version.Version;
import top.vmctcn.vmtu.core.util.version.VersionRange;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MetadataReader {
    private static final String ASSET_ROOT = "https://gitee.com/Wulian233/vmtu/raw/main/resourcepack/";
    private static final Gson GSON = new Gson();
    private static Metadata metadata;
    private static final URI metadataUrl = URI.create(ASSET_ROOT + "metadata.json");

    static {
        metadata = loadFromRemote();
        if (metadata == null) {
            metadata = loadFromLocal();
        }
        if (metadata == null) {
            throw new RuntimeException("Failed to load metadata from both remote and local sources");
        }
    }

    private static Metadata loadFromRemote() {
        try {
            URLConnection connection = metadataUrl.toURL().openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            try (Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, Metadata.class);
            }
        } catch (Exception e) {
            VMTUCore.LOGGER.warn("Failed to load remote metadata.json, falling back to local.", e);
            return null;
        }
    }

    private static Metadata loadFromLocal() {
        try (InputStream is = MetadataReader.class.getResourceAsStream("/metadata.json")) {
            if (is == null) {
                VMTUCore.LOGGER.error("Local metadata.json not found in resources.");
                return null;
            }
            return GSON.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), Metadata.class);
        } catch (Exception e) {
            VMTUCore.LOGGER.error("Failed to load local metadata.json.", e);
            return null;
        }
    }

    private static Metadata.GameMetadata getGameMetaData(String minecraftVersion) {
        Version version = Version.from(minecraftVersion);
        return metadata.games.stream().filter(it -> {
            VersionRange range = new VersionRange(it.gameVersions);
            return range.contains(Objects.requireNonNull(version));
        }).findFirst().orElseThrow(() -> new IllegalStateException(String.format("Version %s not found in meta", minecraftVersion)));
    }

    private static Metadata.AssetMetadata getAssetMetaData(String minecraftVersion) {
        List<Metadata.AssetMetadata> current = metadata.assets.stream()
                .filter(it -> it.targetVersion.equals(minecraftVersion))
                .collect(Collectors.toList());
        return current.stream().findFirst().orElseGet(() -> current.get(0));
    }

    public static GameAssetDetail getAssetDetail(String minecraftVersion) {
        Metadata.GameMetadata convert = getGameMetaData(minecraftVersion);
        GameAssetDetail ret = new GameAssetDetail();
        ret.downloads = createDownloadDetails(convert);
        ret.covertPackFormat = convert.packFormat;
        ret.covertFileName = String.format("VMTranslationPack-Converted-%s.zip", minecraftVersion);
        return ret;
    }

    private static List<GameAssetDetail.AssetDownloadDetail> createDownloadDetails(Metadata.GameMetadata convert) {
        return convert.convertFrom.stream().map(MetadataReader::getAssetMetaData).map(it -> {
            GameAssetDetail.AssetDownloadDetail adi = new GameAssetDetail.AssetDownloadDetail();
            adi.fileName = it.filename;
            adi.fileUrl = ASSET_ROOT + it.filename;
            adi.targetVersion = it.targetVersion;
            return adi;
        }).collect(Collectors.toList());
    }
}
