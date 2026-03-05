package top.vmctcn.vmtu.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum MirrorSources {
    MAXING_CDN("http://cdn.maxing.site/"),
    GITEE("https://gitee.com/Wulian233/vmtu/raw/main/"),
    CNB("https://cnb.cool/VMChineseTranslationGroup/VM-Resources/-/git/raw/main/"),
    GITHUB("https://raw.githubusercontent.com/VM-Chinese-translate-group/VM-Resources/refs/heads/main/");

    private final String mirrorUrl;

    MirrorSources(String mirrorUrl) {
        this.mirrorUrl = mirrorUrl;
    }

    public String getMirrorUrl() {
        return mirrorUrl;
    }

    public static ArrayList<MirrorSources> getAllMirrors() {
        return Arrays.stream(values()).collect(Collectors.toCollection(ArrayList::new));
    }
}
