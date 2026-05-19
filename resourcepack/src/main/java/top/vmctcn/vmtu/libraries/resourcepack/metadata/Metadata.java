package top.vmctcn.vmtu.libraries.resourcepack.metadata;

import java.util.List;

public class Metadata {
    public List<GameMetadata> games;
    public List<AssetMetadata> assets;

    public static class GameMetadata {
        public String gameVersions;
        public Integer packFormat, minFormat, maxFormat;
        public List<String> convertFrom;

        public boolean useNewFormat() {
            return (minFormat != null && maxFormat != null) && packFormat > 65;
        }
    }

    public static class AssetMetadata {
        public String targetVersion;
        public String filename;
    }
}
