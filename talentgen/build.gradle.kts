import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":common"))
    implementation("no.tornado:tornadofx")
}
