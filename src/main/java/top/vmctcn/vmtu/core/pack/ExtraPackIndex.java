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

    public enum IndexPosition {
        TOP,
        BOTTOM,
        CUSTOM
    }
}
