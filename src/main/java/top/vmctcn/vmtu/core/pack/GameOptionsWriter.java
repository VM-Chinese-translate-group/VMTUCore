package top.vmctcn.vmtu.core.pack;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import top.vmctcn.vmtu.core.VMTUCore;
import org.apache.commons.io.FileUtils;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class GameOptionsWriter {
    private static final Gson GSON = new Gson();
    private static final Type STRING_LIST_TYPE = new TypeToken<List<String>>() {
    }.getType();
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

    public void switchLanguage(String langCode) {
        configs.put("lang", langCode);
        VMTUCore.LOGGER.info(String.format("Game Language: %s", configs.get("lang")));
    }

    public int getResourcePacksSize() {
        List<String> resourcePacks = GSON.fromJson(
                configs.computeIfAbsent("resourcePacks", it -> "[]"), STRING_LIST_TYPE);
        return resourcePacks.size();
    }

    public void addResourcePack(
            String baseName,
            String resourcePack,
            String extraResourcePack,
            ExtraPackIndex extraPackIndex,
            int customPackIndex,
            boolean needDownloadResourcePack,
            boolean needLoadExtraResourcePack
    ) {
        List<String> resourcePacks = GSON.fromJson(
                configs.computeIfAbsent("resourcePacks", it -> "[]"), STRING_LIST_TYPE);

        //If resource packs already contains target resource pack, nothing to do
        if (resourcePacks.contains(resourcePack)) {
            return;
        }

        //Remove other VM Pack
        if (needDownloadResourcePack) {
            resourcePacks = resourcePacks.stream().filter(it -> !it.contains(baseName)).collect(Collectors.toList());
        }

        // get Minecraft-Mod-Language-Modpack name in resourcePacks
        String cfpaPackName = "";
        for (String packName : resourcePacks) {
            if (packName.contains("Minecraft-Mod-Language-Modpack")) {
                cfpaPackName = packName;
                VMTUCore.LOGGER.info("Get CFPA pack name: {}", packName);
                break;
            }
        }

        if (needLoadExtraResourcePack && extraResourcePack.length() > 2) {
            // set extra pack index
            switch (extraPackIndex) {
                case TOP_OF_CFPA:
                    //Remove resource pack, we need re-index
                    if (needDownloadResourcePack) {
                        resourcePacks = resourcePacks.stream().filter(it -> {
                            return !it.contains("Minecraft-Mod-Language-Modpack") && !it.contains(extraResourcePack) && !it.contains(resourcePack);
                        }).collect(Collectors.toList());
                    } else {
                        resourcePacks = resourcePacks.stream().filter(it -> {
                            return !it.contains("Minecraft-Mod-Language-Modpack") && !it.contains(extraResourcePack);
                        }).collect(Collectors.toList());
                    }

                    // re-index
                    resourcePacks.add(cfpaPackName);
                    if (needDownloadResourcePack) {
                        resourcePacks.add(resourcePack);
                    }
                    resourcePacks.add("file/" + extraResourcePack);

                    break;
                case BOTTOM_OF_CFPA:
                    //Remove resource pack, we need re-index
                    if (needDownloadResourcePack) {
                        resourcePacks = resourcePacks.stream().filter(it -> {
                            return !it.contains("Minecraft-Mod-Language-Modpack") && !it.contains(extraResourcePack) && !it.contains(resourcePack);
                        }).collect(Collectors.toList());
                    } else {
                        resourcePacks = resourcePacks.stream().filter(it -> {
                            return !it.contains("Minecraft-Mod-Language-Modpack") && !it.contains(extraResourcePack);
                        }).collect(Collectors.toList());
                    }

                    // re-index
                    if (needDownloadResourcePack) {
                        resourcePacks.add(resourcePack);
                    }
                    resourcePacks.add("file/" + extraResourcePack);
                    resourcePacks.add(cfpaPackName);

                    break;
                case CUSTOM_INDEX:
                    //Remove resource pack, we need re-index
                    if (needDownloadResourcePack) {
                        resourcePacks = resourcePacks.stream().filter(it -> {
                            return !it.contains("Minecraft-Mod-Language-Modpack") && !it.contains(extraResourcePack) && !it.contains(resourcePack);
                        }).collect(Collectors.toList());
                    } else {
                        resourcePacks = resourcePacks.stream().filter(it -> {
                            return !it.contains("Minecraft-Mod-Language-Modpack") && !it.contains(extraResourcePack);
                        }).collect(Collectors.toList());
                    }

                    int index = Math.max(0, Math.min(customPackIndex, resourcePacks.size()));

                    // re-index
                    resourcePacks.add(cfpaPackName);
                    if (needDownloadResourcePack) {
                        resourcePacks.add(resourcePack);
                    }
                    resourcePacks.add(index, "file/" + extraResourcePack);

                    break;
            }
        } else {
            //Remove resource pack, we need re-index
            if (needDownloadResourcePack) {
                resourcePacks = resourcePacks.stream().filter(it -> {
                    return !it.contains("Minecraft-Mod-Language-Modpack") && !it.contains(resourcePack);
                }).collect(Collectors.toList());
            } else {
                resourcePacks = resourcePacks.stream().filter(it -> {
                    return !it.contains("Minecraft-Mod-Language-Modpack");
                }).collect(Collectors.toList());
            }

            resourcePacks.add(cfpaPackName);
            if (needDownloadResourcePack) {
                resourcePacks.add(resourcePack);
            }
        }

        configs.put("resourcePacks", GSON.toJson(resourcePacks));
        VMTUCore.LOGGER.info(String.format("Resource Packs: %s", configs.get("resourcePacks")));
    }
}
