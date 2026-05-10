package top.vmctcn.vmtu.libraries.modpack.metadata.impl;

import top.vmctcn.vmtu.libraries.modpack.metadata.api.MetadataType;
import top.vmctcn.vmtu.libraries.modpack.metadata.api.ModpackMetadata;

import java.util.List;

public class SUCMetadataImpl implements ModpackMetadata {
    String project_id;
    String version_id;
    String display_name;
    String display_version;
    List<String> release_type;
    String max_exclusive_version;
    boolean use_version_number_for_display;

    public String getProjectId() {
        return project_id;
    }

    public String getVersionId() {
        return version_id;
    }

    public String getDisplayName() {
        return display_name;
    }

    public String getDisplayVersion() {
        return display_version;
    }

    public List<String> getReleaseType() {
        return release_type;
    }

    public String getMaxExclusiveVersion() {
        return max_exclusive_version;
    }

    public boolean isUseVersionNumberForDisplay() {
        return use_version_number_for_display;
    }

    @Override
    public String getModpackVersion() {
        return display_version;
    }

    @Override
    public String getModpackName() {
        return display_name;
    }

    @Override
    public MetadataType getMetadataType() {
        return MetadataType.SUC;
    }
}
