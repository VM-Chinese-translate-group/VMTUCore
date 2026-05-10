# VMTU Libraries

VMTU Core Libraries.

## Modules

|     Module     | License | Usage                                                                                                                                                                      |
|:--------------:|:-------:|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|    `common`    | LGPL v3 | General-purpose library for modules.[^1]                                                                                                                                   |
|   `modpack`    | LGPL v3 | Designed for modpack environments, featuring modpackinfo and the ability to read metadata included in modpacks.                                                            |
| `resourcepack` | AGPL v3 | A library that downloads and automatically installs resource packs, and also includes a feature for automatically installing local resource packs. Fork of I18nUpdateMod3. |

[^1]: When importing other modules into your project, you must also import the common module.

## How to use on your project
We use Jitpack as our Maven repository.

```groovy
repositories {
    mavenCentral()
    maven { url 'https://www.jitpack.io' }
}

dependencies {
    implementation 'com.github.VM-Chinese-translate-group:VMTULibraries:${module_name}:${version}'
}
```
