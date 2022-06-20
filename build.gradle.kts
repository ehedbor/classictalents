plugins {
    kotlin("jvm") version "1.6.21"
    application
    id("io.freefair.sass-java") version "6.4.3"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
}

group = "org.hedbor.evan"
version = "2.0.6"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3")
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
        name = "ClassicTalents"
    }
    jpackage {
        vendor = "Evan Hedbor"
        imageName = "ClassicTalents"
        installerName = "ClassicTalents"

        installerType = if (project.hasProperty("installerType")) {
            project.properties["installerType"] as String
        } else {
            val currentOs = org.gradle.internal.os.OperatingSystem.current()
            when {
                currentOs.isWindows -> "msi"
                currentOs.isLinux -> "deb"
                currentOs.isMacOsX -> "pkg"
                else -> error("Unknown OS '${currentOs.name}'")
            }
        }

        installerOptions = mutableListOf(
            "--description", "A simple talent calculator for World of Warcraft: Classic.",
            "--copyright", "Copyright (C) Evan Hedbor 2020-2022 (MIT License)",
        )

        when (installerType) {
           "msi", "exe" -> {
               imageOptions.addAll(listOf(
                   "--resource-dir", "jpackage\\windows"
               ))
               installerOptions.addAll(listOf(
                   "--win-per-user-install",
                   "--win-dir-chooser",
                   "--win-shortcut",
                   "--win-shortcut-prompt",
                   "--win-menu",
               ))
           }
            "deb", "rpm" -> {
                imageOptions.addAll(listOf(
                    "--resource-dir", "jpackage/linux"
                ))
                installerOptions.addAll(listOf(
                    "--linux-shortcut",
                    "--linux-menu-group", "Game;Java;",
                ))

                if (installerType == "deb") {
                    installerOptions.addAll(listOf(
                        "--linux-app-category", "games",
                        "--linux-deb-maintainer", "evan@hedbor.org",
                    ))
                } else {
                    installerOptions.addAll(listOf(
                        "--linux-rpm-license-type", "MIT",
                    ))
                }
            }
            "pkg", "dmg" -> {
                imageOptions.addAll(listOf(
                    "--resource-dir", "jpackage/macos"
                ))
            }
        }
    }
    imageZip.set(project.file("${project.buildDir}/image-zip/${project.name}-${project.version}.zip"))
}

// The jlink compile isn't executable since the kotlin packages are declared twice. AFAIK, the
// reason that happens is because a bunch of .kotlin_metadata files are generated--which don't seem
// to be needed to run the program--and put in the mergedJarsDir.  This task fixes that issue by
// just deleting the whole kotlin directory.
val excludeKotlinMetadata = tasks.register("excludeKotlinMetadata") {
    dependsOn(tasks.prepareMergedJarsDir)
    doLast {
        val mergedJarsDir = tasks.prepareMergedJarsDir.get().mergedJarsDir
        val kotlinDir = mergedJarsDir.dir("kotlin")
        delete(kotlinDir)
    }
}

tasks.createMergedModule.configure {
    dependsOn(excludeKotlinMetadata)
}

tasks.jar {
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

tasks.jar.configure {
    exclude("**/*.sass")
    exclude("**/*.scss")
    exclude("**/*.css.map")
    // this doesnt seem to have an effect, is it needed?
    exclude("**/*.kotlin_metadata")
    exclude("**/*.kotlin_module")
    exclude("**/*.kotlin_builtins")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "17"
}