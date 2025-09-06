package top.vmctcn.vmtu.core.pack;

public enum PackIndex {
    TOP_OF_CFPA(true, false),
    BOTTOM_OF_CFPA(false, false),
    CUSTOM_INDEX(false, false)
    ;

    private boolean topOfCfpaPack;
    private boolean customIndex;

    PackIndex(boolean topOfCfpaPack, boolean customIndex) {
        this.topOfCfpaPack = topOfCfpaPack;
        this.customIndex = customIndex;
    }

    public boolean isTopOfCfpaPack() {
        return topOfCfpaPack;
    }

    public boolean isCustomIndex() {
        return customIndex;
    }
}
