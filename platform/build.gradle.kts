plugins {
    `java-platform`
}

dependencies {
    constraints {
        api(kotlin("reflect"))
        api("no.tornado:tornadofx:1.7.20")
        api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    }
}