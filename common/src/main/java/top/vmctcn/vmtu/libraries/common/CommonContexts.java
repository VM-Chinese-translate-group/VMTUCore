package top.vmctcn.vmtu.libraries.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.ServiceLoader;

public class CommonContexts {
    public static final Logger LOGGER = LogManager.getLogger("VMTU-Libraries");

    private static GameInfo gameInfo;

    public static void setGameInfo(String gameVersion, Path gameDir) {
        gameInfo = new GameInfo(gameVersion, gameDir);
    }

    public static GameInfo getGameInfo() {
        return gameInfo;
    }

    public static <T> T loadService(final Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        Iterator<T> iterator = serviceLoader.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        Package servicePackage = clazz.getPackage();
        throw new AssertionError("No impl found for " + (servicePackage != null ? servicePackage.getName() : clazz.getName()));
    }
}
