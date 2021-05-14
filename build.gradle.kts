import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
    
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
    testImplementation(kotlin("test-junit"))
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}