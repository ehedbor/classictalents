pluginManagement {
    val kotlinVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
}


rootProject.name = "classictalents"
include("app", "common", "talentgen", "platform")
