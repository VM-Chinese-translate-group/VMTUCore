package top.vmctcn.vmtu.libraries.resourcepack;

public class ExtraResourcePackInfo {
    private final String name;
    private final int customIndex;
    private final boolean loadPack;

    public ExtraResourcePackInfo(String name, int customExtraPackIndex, boolean loadPack) {
        this.name = name;
        this.customIndex = customExtraPackIndex;
        this.loadPack = loadPack;
    }

    public String getName() {
        return name;
    }

    public int getCustomIndex() {
        return customIndex;
    }

    public boolean isLoadPack() {
        return loadPack;
    }
}
