plugins {
    id("java")
    id("com.gradleup.shadow")
}

base.archivesName = "${libs.versions.core.name.get()}-resourcepack"
version = libs.versions.core.version.get()
group = "top.vmctcn.vmtu.libraries.resourcepack"

dependencies {
    implementation(project(":common"))
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

tasks.shadowJar {
    minimize()
    relocate("com.google.archivepatcher", "include.com.google.archivepatcher")
    dependencies {
        include(dependency("net.runelite.archive-patcher:archive-patcher-applier:.*"))
    }
    exclude("LICENSE")
    archiveClassifier.set(null)
}

tasks.jar {
    archiveClassifier.set("raw")
}