import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.24.4"
}

application {
    mainModule.set("org.hedbor.evan.classictalents")
    mainClass.set("org.hedbor.evan.classictalents.ClassicTalentsAppKt")
}

javafx {
    version = "17.0.1"
    modules = listOf("javafx.controls", "javafx.fxml")
}

jlink {
    launcher {
        name = "org.hedbor.evan.classictalents"
    }
}

group = "org.hedbor.evan"
version = "2.0.0-alpha"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.withType<Jar> {
    archiveBaseName.set(rootProject.name)
    manifest {
        attributes["Name"] = "org/hedbor/evan/classictalents"
        attributes["Specification-Title"] = "Classic Talents"
        attributes["Specification-Version"] = project.version
        attributes["Specification-Vendor"] = "Evan Hedbor"
        attributes["Implementation-Title"] = "org.hedbor.evan.classictalents"
        attributes["Implementation-Version"] = project.version
        attributes["Implementation-Vendor"] = "Evan Hedbor"
    }
}

//tasks.withType<Link> {
//    archiveClassifier.set("")
//}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}