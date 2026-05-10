package top.vmctcn.vmtu.libraries.modpack.info.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import top.vmctcn.vmtu.libraries.common.CommonContexts;
import top.vmctcn.vmtu.libraries.common.LogMarkers;
import top.vmctcn.vmtu.libraries.modpack.info.impl.DefaultModpackInfo;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ModpackInfoHelper {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ModpackInfo modpackInfo;
    private static final Path gameDir = CommonContexts.getGameInfo().getGameDir();
    private static final Path modpackInfoPath = gameDir.resolve("modpackinfo.json");

    private ModpackInfoHelper() {
    }

    public static void readModpackInfo(boolean generateExampleModpackInfo) {
        if (Files.exists(modpackInfoPath)) {
            try (Reader reader = Files.newBufferedReader(modpackInfoPath, StandardCharsets.UTF_8)) {
                modpackInfo = GSON.fromJson(reader, ModpackInfo.class);
                if (modpackInfo == null) {
                    if (generateExampleModpackInfo) {
                        CommonContexts.LOGGER.warn(LogMarkers.MODPACK, "modpackinfo.json is empty or invalid, generating default file.");
                        generateDefaultModpackInfo();
                    } else {
                        CommonContexts.LOGGER.warn(LogMarkers.MODPACK, "modpackinfo.json is empty or invalid, skip it.");
                    }
                }
            } catch (Exception e) {
                if (generateExampleModpackInfo) {
                    CommonContexts.LOGGER.warn(LogMarkers.MODPACK, "Error reading modpackinfo.json, generating default file.", e);
                    generateDefaultModpackInfo();
                } else {
                    CommonContexts.LOGGER.warn(LogMarkers.MODPACK, "Error reading modpackinfo.json, skip it.");
                }
            }
        } else {
            if (generateExampleModpackInfo) {
                CommonContexts.LOGGER.warn(LogMarkers.MODPACK, "modpackinfo.json does not exist, generating default file.");
                generateDefaultModpackInfo();
            } else {
                CommonContexts.LOGGER.warn(LogMarkers.MODPACK, "modpackinfo.json does not exist, skip it.");
            }
        }
    }

    public static void syncModpackVersion(String newVersion) {
        if (newVersion == null || newVersion.isEmpty()) {
            return;
        }

        if (modpackInfo != null && modpackInfo.modpack != null) {
            String oldVersion = modpackInfo.modpack.version;
            modpackInfo.modpack.version = newVersion;

            try (Writer writer = Files.newBufferedWriter(modpackInfoPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write(GSON.toJson(modpackInfo));
                CommonContexts.LOGGER.info(LogMarkers.MODPACK, "Modpack version updated from {} to {}", oldVersion, newVersion);
            } catch (IOException e) {
                CommonContexts.LOGGER.error(LogMarkers.MODPACK, "Failed to update modpack version to {}", newVersion, e);
            }
        } else {
            CommonContexts.LOGGER.error(LogMarkers.MODPACK, "Cannot update modpack version: modpackInfo or modpack is null");
        }
    }

    private static void generateDefaultModpackInfo() {
        modpackInfo = new DefaultModpackInfo();

        try {
            try (Writer writer = Files.newBufferedWriter(modpackInfoPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write(GSON.toJson(modpackInfo));
            }
            CommonContexts.LOGGER.info(LogMarkers.MODPACK, "Default modpackinfo.json generated.");

            // 再次读取以确保正确加载
            try (Reader reader = Files.newBufferedReader(modpackInfoPath, StandardCharsets.UTF_8)) {
                modpackInfo = GSON.fromJson(reader, ModpackInfo.class);
            }
        } catch (IOException e) {
            CommonContexts.LOGGER.error(LogMarkers.MODPACK, "Failed to generate default modpackinfo.json", e);
        }
    }

    public static ModpackInfo getModpackInfo() {
        if (modpackInfo == null) {
            return new DefaultModpackInfo();
        } else {
            return modpackInfo;
        }
    }

    public static boolean isExampleModpackInfo() {
        return getModpackInfo().getModpack().getTranslation().getId().equals("example");
    }
}
