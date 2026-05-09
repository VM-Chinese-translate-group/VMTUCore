plugins {
    id("java")
    id("com.gradleup.shadow") version "9.+"
    id("maven-publish")
}

base.archivesName = libs.versions.core.name.get()
version = libs.versions.core.version.get()
group = "top.vmctcn.vmtu.libraries"

subprojects {
    apply {
        plugin("java")
        plugin("maven-publish")
    }

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(8)
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenCentral()
        maven("https://libraries.minecraft.net/")
        maven("https://maven.fabricmc.net/")
        maven("https://files.minecraftforge.net/maven")
        maven("https://repo.runelite.net/")
    }

    configurations.configureEach {
        isTransitive = false
    }

    dependencies {
        implementation("net.runelite.archive-patcher:archive-patcher-applier:1.2")
        implementation("org.apache.logging.log4j:log4j-api:2.25.2")
        compileOnly("org.jetbrains:annotations:26.0.2")

        implementation("commons-io:commons-io:2.20.0")
        implementation("com.google.code.gson:gson:2.14.0")
    }

    tasks.processResources {
        filesMatching("**") {
            expand(
                "version" to project.version,
            )
        }
    }
}