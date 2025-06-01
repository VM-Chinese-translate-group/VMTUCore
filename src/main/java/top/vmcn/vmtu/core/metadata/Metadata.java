package top.vmcn.vmtu.core.metadata;

import java.util.List;

public class Metadata {
    public String version;
    public List<GameMetadata> games;
    public List<AssetMetadata> assets;

    public static class GameMetadata {
        public String gameVersions;
        public int packFormat;
        public List<String> convertFrom;
    }

    public static class AssetMetadata {
        public String targetVersion;
        public String filename;
    }
}
