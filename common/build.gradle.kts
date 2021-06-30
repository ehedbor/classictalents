import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(platform(project(":platform")))
    api(kotlin("reflect"))
    api("no.tornado:tornadofx")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}
