package top.vmctcn.vmtu.core;

import com.google.gson.Gson;
import top.vmctcn.vmtu.core.pack.*;
import top.vmctcn.vmtu.core.metadata.MetadataReader;
import top.vmctcn.vmtu.core.metadata.GameAssetDetail;
import top.vmctcn.vmtu.core.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VMTUCore {
    public static final String LOCAL_PATH = "vmtu";
    public static final Logger LOGGER = LoggerFactory.getLogger(VMTUCore.class);
    public static final Gson GSON = new Gson();

    public static void init(
            Path minecraftPath,
            String minecraftVersion,
            String packName,
            String extraPackName,
            ExtraPackIndex extraPackIndex,
            int customExtraPackIndex,
            boolean needDownloadResourcePack,
            boolean needLoadExtraResourcePack
    ) {
        LOGGER.debug(String.format("Minecraft path: %s", minecraftPath));
        String localStorage = getLocalStoragePos(minecraftPath);
        LOGGER.debug(String.format("Local Storage Pos: %s", localStorage));

        try {
            Class.forName("com.netease.mc.mod.network.common.Library");
            LOGGER.warn("VMTUCore will get resource pack from Internet, whose content is uncontrolled.");
            LOGGER.warn("This behavior contraries to Netease Minecraft developer content review rule: " +
                    "forbidden the content in game not match the content for reviewing.");
            LOGGER.warn("To follow this rule, VMTUCore won't download any thing.");
            LOGGER.warn("VMTUCore会从互联网获取内容不可控的资源包。");
            LOGGER.warn("这一行为违背了网易我的世界「开发者内容审核制度」：禁止上传与提审内容不一致的游戏内容。");
            LOGGER.warn("为了遵循这一制度，VMTUCore不会下载任何内容。");
            return;
        } catch (ClassNotFoundException ignored) {
        }

        FileUtil.setResourcePackDirPath(minecraftPath.resolve("resourcepacks"));

        int minecraftMajorVersion = Integer.parseInt(minecraftVersion.split("\\.")[1]);

        try {
            //Get asset
            GameAssetDetail assets = MetadataReader.getAssetDetail(minecraftVersion);
            String applyFileName = assets.downloads.get(0).fileName;

            if (needDownloadResourcePack) {
                //Update resource pack
                List<ResourcePack> languagePacks = new ArrayList<>();
                boolean convertNotNeed = assets.downloads.size() == 1 && assets.downloads.get(0).targetVersion.equals(minecraftVersion);
                applyFileName = assets.downloads.get(0).fileName;
                for (GameAssetDetail.AssetDownloadDetail it : assets.downloads) {
                    FileUtil.setTemporaryDirPath(Paths.get(localStorage, "." + LOCAL_PATH, it.targetVersion));
                    ResourcePack languagePack = new ResourcePack(it.fileName, convertNotNeed);
                    languagePack.checkUpdate(it.fileUrl);
                    languagePacks.add(languagePack);
                }

                //Convert resourcepack
                if (!convertNotNeed) {
                    FileUtil.setTemporaryDirPath(Paths.get(localStorage, "." + LOCAL_PATH, minecraftVersion));
                    applyFileName = assets.covertFileName;
                    ResourcePackConverter converter = new ResourcePackConverter(languagePacks, applyFileName);
                    converter.convert(assets.covertPackFormat, getResourcePackDescription(assets.downloads));
                }
            }

            //Apply resource pack
            GameOptionsWriter writer = new GameOptionsWriter(minecraftPath.resolve("options.txt"));
            writer.addResourcePack(
                    packName,
                    (minecraftMajorVersion <= 12 ? "" : "file/") + applyFileName,
                    extraPackName,
                    extraPackIndex,
                    customExtraPackIndex,
                    needDownloadResourcePack,
                    needLoadExtraResourcePack
            );
            writer.writeToFile();
        } catch (Exception e) {
            LOGGER.warn(String.format("Failed to update resource pack: %s", e));
//            e.printStackTrace();
        }
    }

    private static String getResourcePackDescription(List<GameAssetDetail.AssetDownloadDetail> downloads) {
        return downloads.size() > 1 ?
                String.format("该包由%s版本合并\n作者：VM汉化组",
                        downloads.stream().map(it -> it.targetVersion).collect(Collectors.joining("和"))) :
                String.format("该包对应的官方支持版本为%s\n作者：VM汉化组",
                        downloads.get(0).targetVersion);

    }

    public static String getLocalStoragePos(Path minecraftPath) {
        Path userHome = Paths.get(System.getProperty("user.home"));
        Path oldPath = userHome.resolve("." + LOCAL_PATH);
        if (Files.exists(oldPath)) {
            return userHome.toString();
        }

        // https://developer.apple.com/documentation/foundation/url/3988452-applicationsupportdirectory#discussion
        String macAppSupport = System.getProperty("os.name").contains("OS X") ?
                userHome.resolve("Library/Application Support").toString() : null;
        String localAppData = System.getenv("LocalAppData");

        // XDG_DATA_HOME fallbacks to ~/.local/share
        // https://specifications.freedesktop.org/basedir-spec/latest/#variables
        String xdgDataHome = System.getenv("XDG_DATA_HOME");
        if (xdgDataHome == null) {
            xdgDataHome = userHome.resolve(".local/share").toString();
        }

        return Stream.of(localAppData, macAppSupport).filter(
                Objects::nonNull
        ).findFirst().orElse(xdgDataHome);
    }
}