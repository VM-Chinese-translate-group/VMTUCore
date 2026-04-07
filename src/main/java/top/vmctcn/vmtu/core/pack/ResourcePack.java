package top.vmctcn.vmtu.core.pack;

import top.vmctcn.vmtu.core.VMTUCore;
import top.vmctcn.vmtu.core.util.AssetUtil;
import top.vmctcn.vmtu.core.util.DigestUtil;
import top.vmctcn.vmtu.core.util.FileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
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
    private String remoteMd5;

    public ResourcePack(String filename, boolean saveToGame) {
        //If target version is not current version, not save
        this.saveToGame = saveToGame;
        this.filename = filename;
        this.filePath = FileUtil.getResourcePackPath(filename);
        this.tmpFilePath = FileUtil.getTemporaryPath(filename);
        try {
            FileUtil.syncTmpFile(filePath, tmpFilePath, saveToGame);
        } catch (Exception e) {
            VMTUCore.LOGGER.warn("Error while sync temp file {} <-> {}: {}", filePath, tmpFilePath, e);
        }
    }

    public void checkUpdate(String fileUrl, String md5Url) throws IOException, URISyntaxException, NoSuchAlgorithmException {
        if (isUpToDate(md5Url)) {
            VMTUCore.LOGGER.debug("Already up to date.");
            return;
        }
        //In this time, we can only download full file
        downloadFull(fileUrl);
        //In the future, we will download patch file and merge local file
    }

    private boolean isUpToDate(String md5Url) throws IOException, URISyntaxException, NoSuchAlgorithmException {
        //Not exist -> Update
        if (!Files.exists(tmpFilePath)) {
            VMTUCore.LOGGER.debug("Local file {} not exist.", tmpFilePath);
            return false;
        }
        //Last update time not exceed gap -> Not Update
        if (Files.getLastModifiedTime(tmpFilePath).to(TimeUnit.MILLISECONDS)
                > System.currentTimeMillis() - UPDATE_TIME_GAP) {
            VMTUCore.LOGGER.debug("Local file {} has been updated recently.", tmpFilePath);
            return true;
        }
        //Check Update
        return checkMd5(tmpFilePath, md5Url);
    }

    private boolean checkMd5(Path localFile, String md5Url) throws IOException, URISyntaxException, NoSuchAlgorithmException {
        String localMd5 = DigestUtil.md5Hex(localFile);
        if (remoteMd5 == null) {
            remoteMd5 = AssetUtil.getString(md5Url);
        }
        VMTUCore.LOGGER.debug("{} md5: {}, remote md5: {}", localFile, localMd5, remoteMd5);
        return localMd5.equalsIgnoreCase(remoteMd5);
    }

    private void downloadFull(String fileUrl) throws IOException {
        try {
            Path downloadTmp = FileUtil.getTemporaryPath(filename + ".tmp");
            AssetUtil.download(fileUrl, downloadTmp);
            Files.move(downloadTmp, tmpFilePath, StandardCopyOption.REPLACE_EXISTING);
            VMTUCore.LOGGER.debug("Updates temp file: {}", tmpFilePath);
        } catch (Exception e) {
            VMTUCore.LOGGER.warn("Error while downloading: {}", e.getMessage());
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
