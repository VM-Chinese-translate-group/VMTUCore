package top.vmctcn.vmtu.libraries.resourcepack.metadata;

import com.google.gson.Gson;

import top.vmctcn.vmtu.libraries.common.CommonContexts;
import top.vmctcn.vmtu.libraries.common.LogMarkers;
import top.vmctcn.vmtu.libraries.resourcepack.util.AssetUtil;
import top.vmctcn.vmtu.libraries.resourcepack.util.version.Version;
import top.vmctcn.vmtu.libraries.resourcepack.util.version.VersionRange;

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
    private static final Gson GSON = new Gson();
    private static Metadata metadata;
    private static final URI metadataUrl = URI.create(AssetUtil.getFastestResourcePackUrl() + "metadata.json");

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
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            try (Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, Metadata.class);
            }
        } catch (Exception e) {
            CommonContexts.LOGGER.warn(LogMarkers.RESOURCEPACK, "Failed to load remote metadata.json, falling back to local.", e);
            return null;
        }
    }

    private static Metadata loadFromLocal() {
        try (InputStream is = MetadataReader.class.getResourceAsStream("/metadata.json")) {
            if (is == null) {
                CommonContexts.LOGGER.error(LogMarkers.RESOURCEPACK, "Local metadata.json not found in resources.");
                return null;
            }
            return GSON.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), Metadata.class);
        } catch (Exception e) {
            CommonContexts.LOGGER.error(LogMarkers.RESOURCEPACK, "Failed to load local metadata.json.", e);
            return null;
        }
    }

    public static Metadata.GameMetadata getGameMetaData(String minecraftVersion) {
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

        String assetRoot = AssetUtil.getFastestUrl();
        CommonContexts.LOGGER.debug(LogMarkers.RESOURCEPACK, "Using asset root: {}", assetRoot);

        ret.downloads = createDownloadDetails(convert, assetRoot);
        ret.covertFileName = String.format("VMTranslationPack-Converted-%s.zip", minecraftVersion);
        return ret;
    }

    private static List<GameAssetDetail.AssetDownloadDetail> createDownloadDetails(Metadata.GameMetadata convert, String assetRoot) {
        return convert.convertFrom.stream().map(MetadataReader::getAssetMetaData).map(it -> {
            GameAssetDetail.AssetDownloadDetail adi = new GameAssetDetail.AssetDownloadDetail();
            String md5Filename = it.filename != null && it.filename.toLowerCase().endsWith(".zip")
                    ? it.filename.substring(0, it.filename.length() - 4) + ".md5"
                    : null;
            adi.fileName = it.filename;
            adi.fileUrl = assetRoot + "resourcepack/" + it.filename;
            adi.md5Url = assetRoot + "resourcepack/" + md5Filename;
            adi.targetVersion = it.targetVersion;
            return adi;
        }).collect(Collectors.toList());
    }
}
