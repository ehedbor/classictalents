/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2022 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.talentgen

import javafx.scene.image.Image
import javafx.stage.FileChooser
import tornadofx.chooseFile
import java.io.File


private val IMAGE_FILES_FILTER = FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.gif", "*.jpg", "*.png")

internal val APP_RESOURCES_DIRECTORY: File = run {
    val dir = System.getenv("APP_RESOURCES_DIR") ?: throw IllegalStateException("""
        Environment variable APP_RESOURCES_DIR not set!
        Please declare this environment variable and point it to the resources directory of the classictalents app.
        """.trimIndent())
    File(dir.trim()).absoluteFile
}
internal val INITIAL_ICON_DIRECTORY = APP_RESOURCES_DIRECTORY.resolve("images/Classic")
internal val INITIAL_BACKGROUND_DIRECTORY = APP_RESOURCES_DIRECTORY.resolve("images/backgrounds")

internal val UNKNOWN_IMAGE = Image(
    Thread.currentThread().contextClassLoader.getResourceAsStream("INV_Misc_QuestionMark.png"))

internal fun chooseIconFromResources(prompt: String, initialDirectory: File): String? {
    println(initialDirectory)
    val files = chooseFile(prompt, arrayOf(IMAGE_FILES_FILTER), initialDirectory)
    if (files.isEmpty()) return null
    val file = files[0]

    val inResourcesDir = file.canonicalPath.contains(APP_RESOURCES_DIRECTORY.canonicalPath + File.separator)
    if (!inResourcesDir) return null

    var path = file.relativeTo(APP_RESOURCES_DIRECTORY).path
    path = path.replace(File.separatorChar, '/')
    return path
}
