package top.vmctcn.vmtu.core.util;

import java.util.ArrayList;
import java.util.Arrays;

public enum DefaultConfigs {
    VANILLA("Vanilla", "options.txt"),
    DEFAULT_OPTIONS("DefaultOptions", "config/defaultoptions/options.txt"),
    CONFIGURED_DEFAULTS("ConfiguredDefaults", "configureddefaults/options.txt");

    private final String modName;
    private final String optionsFilePath;

    DefaultConfigs(String modName, String optionsFilePath) {
        this.modName = modName;
        this.optionsFilePath = optionsFilePath;
    }

    public String getModName() {
        return modName;
    }

    public String getOptionsFilePath() {
        return optionsFilePath;
    }

    public static ArrayList<DefaultConfigs> getMods() {
        return ArrayUtil.asArrayList(Arrays.stream(values()));
    }
}
