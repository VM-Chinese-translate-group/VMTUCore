package top.vmctcn.vmtu.libraries.modpack.metadata.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import top.vmctcn.vmtu.libraries.common.CommonContexts;
import top.vmctcn.vmtu.libraries.common.LogMarkers;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModpackMetadataReader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path gameDir = CommonContexts.getGameInfo().getGameDir();

    public static ModpackMetadata getMetadata(MetadataType metadataType) {
        Path metadataPath = gameDir.resolve(metadataType.getMetadataFileName());

        if (Files.exists(metadataPath)) {
            try (Reader reader = Files.newBufferedReader(metadataPath, StandardCharsets.UTF_8)) {
                ModpackMetadata metadata = GSON.fromJson(reader, metadataType.getMetadataClass());
                if (metadata == null) {
                    CommonContexts.LOGGER.warn(LogMarkers.MODPACK, "{} ({}) is empty or invalid", metadataType.getMetadataName(), metadataType.getMetadataFileName());
                    return null;
                }
                return metadata;
            } catch (Exception e) {
                CommonContexts.LOGGER.warn(LogMarkers.MODPACK, "Error reading {} ({}) {}", metadataType.getMetadataName(), metadataType.getMetadataFileName(), e);
            }
        } else {
            CommonContexts.LOGGER.warn(LogMarkers.MODPACK, "{} ({}) does not exist", metadataType.getMetadataName(), metadataType.getMetadataFileName());
        }

        return null;
    }

    public static ModpackMetadata getMetadata() {
        for (MetadataType metadataType : MetadataType.values()) {
            ModpackMetadata metadata = getMetadata(metadataType);
            if (metadata != null) {
                return metadata;
            }
        }
        return null;
    }
}
