pluginManagement {
    plugins {
        kotlin("jvm") version "1.6.21"
        kotlin("plugin.serialization") version "1.6.21"
        id("com.github.johnrengelman.shadow") version "7.1.2"
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("tornadofx", "no.tornado:tornadofx:1.7.20")
            library("kotlinx-serialization", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
        }
    }
}

rootProject.name = "classictalents"
include("app", "common", "talentgen")
