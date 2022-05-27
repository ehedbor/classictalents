import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
}

javafx {
    version = "17.0.1"
    modules = listOf("javafx.controls", "javafx.graphics")
}

dependencies {
    implementation(project(":common"))
    implementation(libs.tornadofx)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}