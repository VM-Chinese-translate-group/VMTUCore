package top.vmcn.vmtu.core.metadata;

import com.google.gson.Gson;
import top.vmcn.vmtu.core.pack.PackSource;
import top.vmcn.vmtu.core.VMTUCore;
import top.vmcn.vmtu.core.util.version.Version;
import top.vmcn.vmtu.core.util.version.VersionRange;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MetadataReader {
    private static final Gson GSON = new Gson();
    private static Metadata metadata;

    static {
        init();
    }

    private static void init() {
        try (InputStream is = MetadataReader.class.getResourceAsStream("/metadata.json")) {
            if (is != null) {
                metadata = GSON.fromJson(new InputStreamReader(is), Metadata.class);
            } else {
                VMTUCore.LOGGER.warn("Error getting index: is is null");
            }
        } catch (Exception e) {
            VMTUCore.LOGGER.warn("Error getting index: " + e);
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

    public static GameAssetDetail getAssetDetail(String minecraftVersion, PackSource packSource) {
        Metadata.GameMetadata convert = getGameMetaData(minecraftVersion);
        GameAssetDetail ret = new GameAssetDetail();

        ret.downloads = convert.convertFrom.stream().map(MetadataReader::getAssetMetaData).map(it -> {
            GameAssetDetail.AssetDownloadDetail adi = new GameAssetDetail.AssetDownloadDetail();
            adi.fileName = it.filename;
            adi.fileUrl = packSource.getUrl() + it.filename;
            adi.targetVersion = it.targetVersion;
            return adi;
        }).collect(Collectors.toList());
        ret.covertPackFormat = convert.packFormat;
        ret.covertFileName = String.format("VMTranslationPack-Converted-%s.zip", minecraftVersion);
        return ret;
    }
}
