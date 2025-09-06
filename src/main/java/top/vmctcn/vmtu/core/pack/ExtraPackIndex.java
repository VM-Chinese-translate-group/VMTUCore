package top.vmctcn.vmtu.core.pack;

public enum ExtraPackIndex {
    TOP_OF_CFPA(IndexPosition.TOP),
    BOTTOM_OF_CFPA(IndexPosition.BOTTOM),
    CUSTOM_INDEX(IndexPosition.CUSTOM)
    ;

    private final IndexPosition extraPackIndexPos;

    ExtraPackIndex(IndexPosition extraPackIndexPos) {
        this.extraPackIndexPos = extraPackIndexPos;
    }

    public boolean isTopOfCfpaPack() {
        return extraPackIndexPos == IndexPosition.TOP;
    }

    public boolean isBottomOfCfpaPack() {
        return extraPackIndexPos == IndexPosition.BOTTOM;
    }

    public boolean isCustomIndex() {
        return extraPackIndexPos == IndexPosition.CUSTOM;
    }

    public enum IndexPosition {
        TOP,
        BOTTOM,
        CUSTOM
    }
}
