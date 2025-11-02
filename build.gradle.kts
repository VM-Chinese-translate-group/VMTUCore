plugins {
    id("java")
    id("com.gradleup.shadow") version "8.+"
    id("maven-publish")
}

group = libs.versions.core.group.get()
version = libs.versions.core.version.get()

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.shadowJar {
    minimize()
    archiveBaseName.set(libs.versions.core.name)
    relocate("com.google.archivepatcher", "include.com.google.archivepatcher")
    dependencies {
        include(dependency("net.runelite.archive-patcher:archive-patcher-applier:.*"))
    }
    exclude("LICENSE")
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
    implementation(libs.archive.patcher.applier)
    implementation(libs.log4j)
    compileOnly(libs.jetbrains.annotations)

    implementation(libs.commons.io)
    implementation(libs.gson)
}

tasks.processResources {
    filesMatching("**") {
        expand(
            "version" to project.version,
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifacts.artifact(tasks.shadowJar)
        }
    }

    repositories {
    }
}