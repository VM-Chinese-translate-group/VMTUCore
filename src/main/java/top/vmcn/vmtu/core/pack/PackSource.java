package top.vmcn.vmtu.core.pack;

public enum PackSource {
    GITEE("https://gitee.com/Wulian233/vmtu/raw/main/resourcepack/");

    private final String sourceUrl;

    PackSource(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getUrl() {
        return this.sourceUrl;
    }
}
