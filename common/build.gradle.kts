plugins {
    id("java")
}

base.archivesName = "${libs.versions.core.name.get()}-common"
group = "top.vmctcn.vmtu.libraries.common"
version = libs.versions.core.version.get()

dependencies {

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