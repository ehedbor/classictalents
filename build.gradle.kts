plugins {
    `java-platform`
    kotlin("jvm") apply false
}

allprojects {
    group = "org.hedbor.evan"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}