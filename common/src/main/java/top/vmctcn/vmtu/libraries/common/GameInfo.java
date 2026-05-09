package top.vmctcn.vmtu.libraries.common;

import java.nio.file.Path;

public class GameInfo {
    private final String gameVersion;
    private final Path gameDir;

    public GameInfo(String gameVersion, Path gameDir) {
        this.gameVersion = gameVersion;
        this.gameDir = gameDir;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public Path getGameDir() {
        return gameDir;
    }
}
