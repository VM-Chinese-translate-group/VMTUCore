package top.vmctcn.vmtu.libraries.modpack.metadata.api;

import top.vmctcn.vmtu.libraries.modpack.metadata.impl.FTBMetadataImpl;
import top.vmctcn.vmtu.libraries.modpack.metadata.impl.MPUCMetadataImpl;
import top.vmctcn.vmtu.libraries.modpack.metadata.impl.SMUCMetadataImpl;
import top.vmctcn.vmtu.libraries.modpack.metadata.impl.SUCMetadataImpl;

public enum MetadataType {
    FTB("config/metadata.json", "FTBModpackMetadata", new FTBMetadataImpl(), FTBMetadataImpl.class),
    MPUC("config/modpack-update-checker/config.json", "ModpackUpdateCheckerMetadata", new MPUCMetadataImpl(), MPUCMetadataImpl.class),
    SMUC("config/simple-modpack-update-checker.json", "SimpleModpackUpdateCheckerConfiguration", new SMUCMetadataImpl(), SMUCMetadataImpl.class),
    SUC("config/simpleupdatechecker_modpack.json", "SimpleUpdateCheckerConfiguration", new SUCMetadataImpl(), SUCMetadataImpl.class);

    private final String metadataFileName;
    private final String metadataName;
    private final ModpackMetadata metadata;
    private final Class<? extends ModpackMetadata> metadataClass;

    MetadataType(String metadataFileName, String metadataName, ModpackMetadata metadata, Class<? extends ModpackMetadata> metadataClass) {
        this.metadataFileName = metadataFileName;
        this.metadataName = metadataName;
        this.metadata = metadata;
        this.metadataClass = metadataClass;
    }

    public String getMetadataFileName() {
        return metadataFileName;
    }

    public String getMetadataName() {
        return metadataName;
    }

    public ModpackMetadata getMetadata() {
        return metadata;
    }

    public Class<? extends ModpackMetadata> getMetadataClass() {
        return metadataClass;
    }
}
