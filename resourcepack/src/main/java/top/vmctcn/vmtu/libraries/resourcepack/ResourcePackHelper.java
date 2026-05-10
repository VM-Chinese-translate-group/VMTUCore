package top.vmctcn.vmtu.libraries.resourcepack;

import top.vmctcn.vmtu.libraries.common.CommonContexts;
import top.vmctcn.vmtu.libraries.common.LogMarkers;
import top.vmctcn.vmtu.libraries.resourcepack.pack.*;
import top.vmctcn.vmtu.libraries.resourcepack.metadata.MetadataReader;
import top.vmctcn.vmtu.libraries.resourcepack.metadata.GameAssetDetail;
import top.vmctcn.vmtu.libraries.resourcepack.util.DefaultConfigs;
import top.vmctcn.vmtu.libraries.resourcepack.util.FileUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourcePackHelper {
    public static final String LOCAL_PATH = "vmtu";
    private static final Path gameDir = CommonContexts.getGameInfo().getGameDir();
    private static final String gameVersion = CommonContexts.getGameInfo().getGameVersion();

    public static void init(ExtraResourcePackInfo extraPackInfo, ResourcePackInfo resourcePackInfo) {
        CommonContexts.LOGGER.debug(LogMarkers.RESOURCEPACK, String.format("Minecraft path: %s", gameDir));
        String localStorage = getLocalStoragePos();
        CommonContexts.LOGGER.debug(LogMarkers.RESOURCEPACK, String.format("Local Storage Pos: %s", localStorage));

        neteaseWarn();

        FileUtil.setResourcePackDirPath(gameDir.resolve("resourcepacks"));

        loadTranslationPack(localStorage, extraPackInfo, resourcePackInfo);
    }

    public static void loadTranslationPack(String localStorage, ExtraResourcePackInfo extraPackInfo, ResourcePackInfo resourcePackInfo) {
        String[] gameVersionParts = gameVersion.split("\\.");
        int gameMajorVersion = Integer.parseInt(gameVersionParts.length > 2 ? gameVersionParts[1] : gameVersionParts[0]);

        try {
            //Get asset
            GameAssetDetail assets = MetadataReader.getAssetDetail(gameVersion);
            String applyFileName = assets.downloads.get(0).fileName;

            if (resourcePackInfo.isDownloadPack()) {
                //Update resource pack
                List<ResourcePack> languagePacks = new ArrayList<>();
                boolean convertNotNeed = assets.downloads.size() == 1 && assets.downloads.get(0).targetVersion.equals(gameVersion);
                applyFileName = assets.downloads.get(0).fileName;
                for (GameAssetDetail.AssetDownloadDetail it : assets.downloads) {
                    FileUtil.setTemporaryDirPath(Paths.get(localStorage, "." + LOCAL_PATH, it.targetVersion));
                    ResourcePack languagePack = new ResourcePack(it.fileName, convertNotNeed);
                    languagePack.checkUpdate(it.fileUrl, it.md5Url);
                    languagePacks.add(languagePack);
                }

                //Convert resourcepack
                if (!convertNotNeed) {
                    FileUtil.setTemporaryDirPath(Paths.get(localStorage, "." + LOCAL_PATH, gameVersion));
                    applyFileName = assets.covertFileName;
                    ResourcePackConverter converter = new ResourcePackConverter(languagePacks, applyFileName);
                    converter.convert(assets.covertPackFormat, getResourcePackDescription(assets.downloads));
                }
            }

            //Apply resource pack
            for (DefaultConfigs mod : DefaultConfigs.getMods()) {
                if (mod.getOptionsFilePath() != null) {
                    CommonContexts.LOGGER.info(LogMarkers.RESOURCEPACK, "Checked {} file", mod.getOptionsFilePath());

                    GameOptionsWriter writer = new GameOptionsWriter(gameDir.resolve(mod.getOptionsFilePath()));

                    if (writer.getConfigs().get("resourcePacks") != null) {
                        writer.addResourcePack(gameMajorVersion, applyFileName, resourcePackInfo, extraPackInfo);
                        writer.writeToFile();
                    }
                }
            }
        } catch (Exception e) {
            CommonContexts.LOGGER.error(LogMarkers.RESOURCEPACK, "Failed to update resource pack: %s", e);
        }
    }

    private static void neteaseWarn() {
        try {
            Class.forName("com.netease.mc.mod.network.common.Library");
            CommonContexts.LOGGER.warn(LogMarkers.RESOURCEPACK, "VMTU-Libraries will get resource pack from Internet, whose content is uncontrolled.");
            CommonContexts.LOGGER.warn(LogMarkers.RESOURCEPACK, "This behavior contraries to Netease Minecraft developer content review rule: " +
                    "forbidden the content in game not match the content for reviewing.");
            CommonContexts.LOGGER.warn(LogMarkers.RESOURCEPACK, "To follow this rule, VMTU-Libraries won't download any thing.");
            CommonContexts.LOGGER.warn(LogMarkers.RESOURCEPACK, "VMTU-Libraries会从互联网获取内容不可控的资源包。");
            CommonContexts.LOGGER.warn(LogMarkers.RESOURCEPACK, "这一行为违背了网易我的世界「开发者内容审核制度」：禁止上传与提审内容不一致的游戏内容。");
            CommonContexts.LOGGER.warn(LogMarkers.RESOURCEPACK, "为了遵循这一制度，VMTU-Libraries不会下载任何内容。");
        } catch (ClassNotFoundException ignored) {
        }
    }

    private static String getResourcePackDescription(List<GameAssetDetail.AssetDownloadDetail> downloads) {
        return downloads.size() > 1 ?
                String.format("该包由%s版本合并\n作者：VM汉化组",
                        downloads.stream().map(it -> it.targetVersion).collect(Collectors.joining("和"))) :
                String.format("该包对应的官方支持版本为%s\n作者：VM汉化组",
                        downloads.get(0).targetVersion);

    }

    public static String getLocalStoragePos() {
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