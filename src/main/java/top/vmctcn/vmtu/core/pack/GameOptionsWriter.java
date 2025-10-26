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

    public void addResourcePack(String baseName, String resourcePack, String extraResourcePack, ExtraPackIndex extraPackIndex, int customPackIndex) {
        List<String> resourcePacks = GSON.fromJson(
                configs.computeIfAbsent("resourcePacks", it -> "[]"), STRING_LIST_TYPE);
        //If resource packs already contains target resource pack, nothing to do
        if (resourcePacks.contains(resourcePack)) {
            return;
        }
        //Remove other VM Pack
        resourcePacks = resourcePacks.stream().filter(it -> !it.contains(baseName)).collect(Collectors.toList());

        if (extraResourcePack.length() > 2) {
            int resourcePacksIndex = resourcePacks.size();

            // get Minecraft-Mod-Language-Modpack name in resourcePacks
            String cfpaPackName = "";
            for (String packName : resourcePacks) {
                if (packName.contains("Minecraft-Mod-Language-Modpack")) {
                    cfpaPackName = packName;
                    VMTUCore.LOGGER.info("Get CFPA pack name: {}", packName);
                    break;
                }
            }

            // set extra pack index
            switch (extraPackIndex) {
                case TOP_OF_CFPA:
                    //Remove other Minecraft-Mod-Language-Modpack, we need re-index
                    resourcePacks = resourcePacks.stream().filter(it -> !it.contains("Minecraft-Mod-Language-Modpack")).collect(Collectors.toList());

                    // re-index
                    resourcePacks.add("file/" + extraResourcePack);
                    resourcePacks.add(cfpaPackName);
                    resourcePacks.add(resourcePack);
                    break;
                case BOTTOM_OF_CFPA:
                    resourcePacks = resourcePacks.stream().filter(it -> !it.contains("Minecraft-Mod-Language-Modpack")).collect(Collectors.toList());

                    resourcePacks.add(cfpaPackName);

                    // get Minecraft-Mod-Language-Modpack index in resourcePacks
                    int cfpaIndex = -1;
                    for (int i = 0; i < resourcePacks.size(); i++) {
                        if (resourcePacks.get(i).contains("Minecraft-Mod-Language-Modpack")) {
                            cfpaIndex = i;
                        }
                    }

                    // if found Minecraft-Mod-Language-Modpack index in resourcePacks, put VM Pack bottom of Minecraft-Mod-Language-Modpack
                    // else use top index
                    if (cfpaIndex != -1) {
                        int index = Math.max(0, Math.min(cfpaIndex + 1, resourcePacksIndex));
                        resourcePacks.add("file/" + extraResourcePack);
                        resourcePacks.add(index + 1, resourcePack);
                    } else {
                        resourcePacks.add(cfpaPackName);
                        resourcePacks.add("file/" + extraResourcePack);
                        resourcePacks.add(resourcePack);
                    }
                    break;
                case CUSTOM_INDEX:
                    int index = Math.max(0, Math.min(customPackIndex, resourcePacksIndex));
                    resourcePacks.add("file/" + extraResourcePack);
                    resourcePacks.add(index + 1, resourcePack);
                    break;
            }
        } else {
            resourcePacks.add(resourcePack);
        }

        configs.put("resourcePacks", GSON.toJson(resourcePacks));
        VMTUCore.LOGGER.info(String.format("Resource Packs: %s", configs.get("resourcePacks")));
    }
}
