plugins {
    kotlin("jvm") 
}

group = "org.hedbor.evan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("com.beust:klaxon:5.5")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}