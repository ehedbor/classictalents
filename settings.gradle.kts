pluginManagement {
    plugins {
        kotlin("jvm") version "1.6.21"
        kotlin("plugin.serialization") version "1.6.21"
        id("org.openjfx.javafxplugin") version "0.0.10"
        id("com.github.johnrengelman.shadow") version "7.1.2"
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("tornadofx", "no.tornado:tornadofx:2.0.0-SNAPSHOT")
            library("kotlinx-serialization", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
        }
    }
}

rootProject.name = "classictalents"
include("app", "common", "talentgen")
