plugins {
    id("java")
    id("com.gradleup.shadow") version "8.+"
    id("maven-publish")
}

group = "VMTUCore"
version = project.properties["version"].toString()

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.shadowJar {
    minimize()
    archiveBaseName.set("VMTUCore")
    relocate("com.google.archivepatcher", "include.com.google.archivepatcher")
    relocate("org.slf4j", "include.org.slf4j")
    dependencies {
        include(dependency("net.runelite.archive-patcher:archive-patcher-applier:.*"))
        include(dependency("org.slf4j:slf4j-api:.*"))
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
    implementation("net.runelite.archive-patcher:archive-patcher-applier:1.2")
    implementation("org.slf4j:slf4j-api:2.0.17")
    compileOnly("org.jetbrains:annotations:26.0.2")

    implementation("commons-io:commons-io:2.19.0")
    implementation("com.google.code.gson:gson:2.13.1")

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