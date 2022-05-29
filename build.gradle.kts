plugins {
    `java-platform`
    kotlin("jvm") apply false
}

allprojects {
    group = "org.hedbor.evan"
    version = "0.2.0"

    repositories {
        mavenCentral()
    }
}