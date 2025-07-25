package top.vmctcn.vmtu.core.pack;

import top.vmctcn.vmtu.core.VMTUCore;
import top.vmctcn.vmtu.core.util.AssetUtil;
import top.vmctcn.vmtu.core.util.FileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

public class ResourcePack {
    /**
     * Limit update check frequency
     */
    private static final long UPDATE_TIME_GAP = TimeUnit.DAYS.toMillis(1);
    private final String filename;
    private final Path filePath;
    private final Path tmpFilePath;
    private final boolean saveToGame;

    public ResourcePack(String filename, boolean saveToGame) {
        //If target version is not current version, not save
        this.saveToGame = saveToGame;
        this.filename = filename;
        this.filePath = FileUtil.getResourcePackPath(filename);
        this.tmpFilePath = FileUtil.getTemporaryPath(filename);
        try {
            FileUtil.syncTmpFile(filePath, tmpFilePath, saveToGame);
        } catch (Exception e) {
            VMTUCore.LOGGER.warn(String.format("Error while sync temp file %s <-> %s: %s", filePath, tmpFilePath, e));
        }
    }

    public void checkUpdate(String fileUrl) throws IOException {
        //In this time, we can only download full file
        downloadFull(fileUrl);
        //In the future, we will download patch file and merge local file
    }

    private void downloadFull(String fileUrl) throws IOException {
        try {
            Path downloadTmp = FileUtil.getTemporaryPath(filename + ".tmp");
            AssetUtil.download(fileUrl, downloadTmp);
            Files.move(downloadTmp, tmpFilePath, StandardCopyOption.REPLACE_EXISTING);
            VMTUCore.LOGGER.debug(String.format("Updates temp file: %s", tmpFilePath));
        } catch (Exception e) {
            VMTUCore.LOGGER.warn("Error while downloading: %s", e);
        }
        if (!Files.exists(tmpFilePath)) {
            throw new FileNotFoundException("Tmp file not found.");
        }
        FileUtil.syncTmpFile(filePath, tmpFilePath, saveToGame);
    }

    public Path getTmpFilePath() {
        return tmpFilePath;
    }

    public String getFilename() {
        return filename;
    }
}
