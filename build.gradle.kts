plugins {
    id("java")
    id("com.gradleup.shadow") version "8.+"
    id("com.modrinth.minotaur") version "2.8.4"
    id("io.github.CDAGaming.cursegradle") version "1.6.1"
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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.3")
    implementation("net.runelite.archive-patcher:archive-patcher-applier:1.2")
    implementation("org.slf4j:slf4j-api:2.0.17")
    compileOnly("org.jetbrains:annotations:24.1.0")

    //implementation("net.fabricmc:fabric-loader:0.15.9")
    //implementation("cpw.mods:modlauncher:8.1.3")
    //implementation("net.minecraft:launchwrapper:1.12")

    implementation("commons-io:commons-io:2.16.1")
    //implementation("org.ow2.asm:asm:9.7")
    implementation("com.google.code.gson:gson:2.11.0")

}

tasks.test {
    useJUnitPlatform()
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