package top.vmcn.vmtu.core.metadata;

import java.util.List;

public class GameAssetDetail {
    public List<AssetDownloadDetail> downloads;
    public Integer covertPackFormat;
    public String covertFileName;

    public static class AssetDownloadDetail {
        public String fileName;
        public String fileUrl;
        public String targetVersion;
    }
}
