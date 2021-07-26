plugins {
    `java-platform`
}

dependencies {
    constraints {
        api(kotlin("reflect"))
        api("no.tornado:tornadofx:1.7.20")
        api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
        api("io.kotest:kotest-runner-junit5:4.6.1")
        api("io.kotest:kotest-assertions-core-jvm:4.6.1")
        api("io.kotest:kotest-property-jvm:4.6.1")
    }
}