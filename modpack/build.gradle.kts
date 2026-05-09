plugins {
    id("java")
}

base.archivesName = "${libs.versions.core.name.get()}-modpack"
group = "top.vmctcn.vmtu.libraries.modpack"
version = libs.versions.core.version.get()

dependencies {
    implementation(project(":common"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifacts.artifact(tasks.jar)
        }
    }

    repositories {
    }
}