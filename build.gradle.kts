plugins {
    `java-platform`
    kotlin("jvm") apply false
}

allprojects {
    group = "org.hedbor.evan"
    version = "0.3.0"

    repositories {
        mavenCentral()
    }
}