import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow")
}

application {
    mainClass.set("org.hedbor.evan.classictalents.app.LauncherKt")
}

dependencies {
    implementation(project(":common"))
    implementation(libs.tornadofx)
}

tasks.withType<Jar> {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("nolibs")
    manifest {
        attributes["Name"] = "org/hedbor/evan/classictalents"
        attributes["Specification-Title"] = "Classic Talents"
        attributes["Specification-Version"] = rootProject.version
        attributes["Specification-Vendor"] = "Evan Hedbor"
        attributes["Implementation-Title"] = "org.hedbor.evan.classictalents"
        attributes["Implementation-Version"] = rootProject.version
        attributes["Implementation-Vendor"] = "Evan Hedbor"
    }
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}
