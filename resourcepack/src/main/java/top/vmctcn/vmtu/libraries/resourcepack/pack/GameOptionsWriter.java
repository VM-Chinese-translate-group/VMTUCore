package top.vmctcn.vmtu.libraries.resourcepack.pack;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import top.vmctcn.vmtu.libraries.common.CommonContexts;
import top.vmctcn.vmtu.libraries.common.LogMarkers;
import top.vmctcn.vmtu.libraries.resourcepack.ExtraResourcePackInfo;
import top.vmctcn.vmtu.libraries.resourcepack.ResourcePackInfo;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class GameOptionsWriter {
    private static final Gson GSON = new Gson();
    private static final Type STRING_LIST_TYPE = new TypeToken<List<String>>(){}.getType();
    protected Map<String, String> configs = new LinkedHashMap<>();
    private final Path configFile;

    public GameOptionsWriter(Path configFile) throws Exception {
        this.configFile = configFile;
        if (!Files.exists(configFile)) {
            return;
        }
        this.configs = FileUtils.readLines(configFile.toFile(), StandardCharsets.UTF_8).stream()
                .map(it -> it.split(":", 2))
                .filter(it -> it.length == 2)
                .collect(Collectors.toMap(it -> it[0], it -> it[1], (a, b) -> a, LinkedHashMap::new));
    }

    public void writeToFile() throws Exception {
        FileUtils.writeLines(configFile.toFile(), "UTF-8", configs.entrySet().stream()
                .map(it -> it.getKey() + ":" + it.getValue()).collect(Collectors.toList()));
    }

    public Map<String, String> getConfigs() {
        return this.configs;
    }

    public void switchLanguage(String langCode) {
        configs.put("lang", langCode);
        CommonContexts.LOGGER.info(LogMarkers.RESOURCEPACK, String.format("Game Language: %s", configs.get("lang")));
    }

    public int getResourcePacksSize() {
        List<String> resourcePacks = GSON.fromJson(
                configs.computeIfAbsent("resourcePacks", it -> "[]"), STRING_LIST_TYPE);
        return resourcePacks.size();
    }

    public void addResourcePack(int gameMajorVersion, String resourcePackName, ResourcePackInfo resourcePackInfo, ExtraResourcePackInfo extraPackInfo) {
        boolean canLoadExtraPack = false;
        String resourcePack = (gameMajorVersion <= 12 ? "" : "file/") + resourcePackName;
        boolean needDownloadResourcePack = resourcePackInfo.isDownloadPack();
        String extraResourcePack = (gameMajorVersion <= 12 ? "" : "file/") + extraPackInfo.getName();
        int customPackIndex = extraPackInfo.getCustomIndex();

        List<String> resourcePacks = GSON.fromJson(
                configs.computeIfAbsent("resourcePacks", it -> "[]"), STRING_LIST_TYPE);

        //If resource packs already contains target resource pack, nothing to do
        if (resourcePacks.contains(resourcePack)) {
            return;
        }

        //Remove other VM Pack
        if (resourcePackInfo.isDownloadPack()) {
            // get Legacy VM pack name in resourcePacks
            String legacyPackName = null;
            for (String packName : resourcePacks) {
                if (packName.contains("VM汉化组模组汉化包")) {
                    legacyPackName = packName;
                    CommonContexts.LOGGER.info(LogMarkers.RESOURCEPACK, "Get legacy VM pack name: {}", packName);
                    break;
                }
            }
            if (legacyPackName != null) {
                String finalLegacyPackName = legacyPackName;
                resourcePacks = resourcePacks.stream().filter(it -> !it.contains("VMTranslationPack") && !it.contains(finalLegacyPackName)).collect(Collectors.toList());
            } else {
                resourcePacks = resourcePacks.stream().filter(it -> !it.contains("VMTranslationPack")).collect(Collectors.toList());
            }
        }

        // get Minecraft-Mod-Language-Modpack name in resourcePacks
        String cfpaPackName = null;
        for (String packName : resourcePacks) {
            if (packName.contains("Minecraft-Mod-Language-Modpack")) {
                cfpaPackName = packName;
                CommonContexts.LOGGER.info(LogMarkers.RESOURCEPACK, "Get CFPA pack name: {}", packName);
                break;
            }
        }

        if (extraPackInfo.isLoadPack() && extraResourcePack != null && extraResourcePack.isEmpty()) {
            canLoadExtraPack = true;
        }

        // set pack index
        switch (resourcePackInfo.getIndex()) {
            case TOP_OF_CFPA:
                //Remove resource pack, we need re-index
                resourcePacks = removeResourcePacks(resourcePacks, resourcePack, extraResourcePack, needDownloadResourcePack, canLoadExtraPack);

                // re-index
                setResourcePacks(resourcePacks, cfpaPackName, resourcePack, extraResourcePack, needDownloadResourcePack, canLoadExtraPack, ResourcePackIndex.TOP_OF_CFPA, 0);

                break;
            case BOTTOM_OF_CFPA:
                //Remove resource pack, we need re-index
                resourcePacks = removeResourcePacks(resourcePacks, resourcePack, extraResourcePack, needDownloadResourcePack, canLoadExtraPack);

                // re-index
                setResourcePacks(resourcePacks, cfpaPackName, resourcePack, extraResourcePack, needDownloadResourcePack, canLoadExtraPack, ResourcePackIndex.BOTTOM_OF_CFPA, 0);

                break;
            case CUSTOM_INDEX:
                //Remove resource pack, we need re-index
                resourcePacks = removeResourcePacks(resourcePacks, resourcePack, extraResourcePack, needDownloadResourcePack, canLoadExtraPack);

                int index = Math.max(0, Math.min(extraPackInfo.getCustomIndex(), resourcePacks.size()));

                // re-index
                setResourcePacks(resourcePacks, cfpaPackName, resourcePack, extraResourcePack, needDownloadResourcePack, canLoadExtraPack, ResourcePackIndex.CUSTOM_INDEX, index);

                break;

            case DEFAULT:
            default:
                //Remove resource pack, we need re-index
                resourcePacks = removeResourcePacks(resourcePacks, resourcePack, null, needDownloadResourcePack, false);

                // re-index
                setResourcePacks(resourcePacks, cfpaPackName, resourcePack, null, needDownloadResourcePack, false, ResourcePackIndex.DEFAULT, 0);

                break;
        }

        configs.put("resourcePacks", GSON.toJson(resourcePacks));
        CommonContexts.LOGGER.info(LogMarkers.RESOURCEPACK, "Resource Packs: {}", configs.get("resourcePacks"));
    }

    public static List<String> removeResourcePacks(List<String> resourcePacks, String resourcePackName, String extraPackName, boolean canDownloadResourcePack, boolean canLoadExtraPack) {
        if (canDownloadResourcePack) {
            if (extraPackName != null) {
                return resourcePacks.stream().filter(it -> !it.contains("Minecraft-Mod-Language-Modpack") && !it.contains(extraPackName) && !it.contains(resourcePackName)).collect(Collectors.toList());
            } else {
                return resourcePacks.stream().filter(it -> !it.contains("Minecraft-Mod-Language-Modpack") && !it.contains(resourcePackName)).collect(Collectors.toList());
            }
        } else if (canLoadExtraPack && extraPackName != null) {
            return resourcePacks.stream().filter(it -> !it.contains("Minecraft-Mod-Language-Modpack") && !it.contains(extraPackName)).collect(Collectors.toList());
        } else {
            return resourcePacks.stream().filter(it -> !it.contains("Minecraft-Mod-Language-Modpack")).collect(Collectors.toList());
        }
    }

    public static void setResourcePacks(List<String> resourcePacks, String cfpaPackName, String resourcePackName, String extraPackName, boolean canDownloadResourcePack, boolean canLoadExtraPack, ResourcePackIndex resourcePackIndex, int customIndex) {
        switch (resourcePackIndex) {
            case TOP_OF_CFPA:
                if (cfpaPackName != null) {
                    resourcePacks.add(cfpaPackName);
                }
                if (canDownloadResourcePack) {
                    resourcePacks.add(resourcePackName);
                }
                if (canLoadExtraPack) {
                    resourcePacks.add(extraPackName);
                }
                break;
            case BOTTOM_OF_CFPA:
                if (canDownloadResourcePack) {
                    resourcePacks.add(resourcePackName);
                }
                if (canLoadExtraPack) {
                    resourcePacks.add(extraPackName);
                }
                if (cfpaPackName != null) {
                    resourcePacks.add(cfpaPackName);
                }
                break;
            case CUSTOM_INDEX:
                if (cfpaPackName != null) {
                    resourcePacks.add(cfpaPackName);
                }
                if (canDownloadResourcePack) {
                    resourcePacks.add(resourcePackName);
                }
                if (canLoadExtraPack) {
                    resourcePacks.add(customIndex, extraPackName);
                }
                break;
            case DEFAULT:
            default:
                if (cfpaPackName != null) {
                    resourcePacks.add(cfpaPackName);
                }
                if (canDownloadResourcePack) {
                    resourcePacks.add(resourcePackName);
                }
                break;
        }
    }
}
