package top.vmctcn.vmtu.libraries.modpack.metadata.impl;

import top.vmctcn.vmtu.libraries.modpack.metadata.api.MetadataType;
import top.vmctcn.vmtu.libraries.modpack.metadata.api.ModpackMetadata;

public class SMUCMetadataImpl implements ModpackMetadata {
    int configVersion;
    String localVersion;
    String identifier;

    public int getConfigVersion() {
        return configVersion;
    }

    public String getLocalVersion() {
        return localVersion;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getModpackVersion() {
        return localVersion;
    }

    @Override
    public String getModpackName() {
        return ""; // SMUC NOT SUPPORTED
    }

    @Override
    public MetadataType getMetadataType() {
        return MetadataType.SMUC;
    }
}
