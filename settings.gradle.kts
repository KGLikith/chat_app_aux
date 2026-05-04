plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "server"
include("src:main:proto")
findProject(":src:main:proto")?.name = "proto"
