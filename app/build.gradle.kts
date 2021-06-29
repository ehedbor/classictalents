import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
}

group = "org.hedbor.evan"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":common"))
    implementation("no.tornado:tornadofx:1.7.20")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}