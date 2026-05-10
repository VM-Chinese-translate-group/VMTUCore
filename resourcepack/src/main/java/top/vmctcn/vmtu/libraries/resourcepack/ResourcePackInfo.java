package top.vmctcn.vmtu.libraries.resourcepack;

import top.vmctcn.vmtu.libraries.resourcepack.pack.ResourcePackIndex;

public class ResourcePackInfo {
    private final ResourcePackIndex index;
    private final boolean downloadPack;

    public ResourcePackInfo(ResourcePackIndex index, boolean downloadPack) {
        this.index = index;
        this.downloadPack = downloadPack;
    }

    public ResourcePackIndex getIndex() {
        return index;
    }

    public boolean isDownloadPack() {
        return downloadPack;
    }
}
