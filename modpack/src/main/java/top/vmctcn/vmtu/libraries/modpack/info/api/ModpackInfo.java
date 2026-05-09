package top.vmctcn.vmtu.libraries.modpack.info.api;

public class ModpackInfo {
    public Modpack modpack;

    public Modpack getModpack() {
        return modpack;
    }

    public static class Modpack {
        public String name;
        public String version;
        public Translation translation;

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public Translation getTranslation() {
            return translation;
        }
    }

    public static class Translation {
        public String id;
        public String url;
        public String language;
        public String version;
        @Deprecated
        public String updateCheckUrl;
        @Deprecated
        public String resourcePackName;

        public String getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }

        public String getLanguage() {
            return language;
        }

        public String getVersion() {
            return version;
        }

        @Deprecated
        public String getUpdateCheckUrl() {
            return updateCheckUrl;
        }

        @Deprecated
        public String getResourcePackName() {
            return resourcePackName;
        }
    }
}
