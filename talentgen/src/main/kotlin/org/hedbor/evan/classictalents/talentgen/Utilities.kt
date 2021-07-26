package org.hedbor.evan.classictalents.talentgen

import javafx.stage.FileChooser
import tornadofx.chooseFile
import java.io.File


private val IMAGE_FILES_FILTER = FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.gif", "*.jpg", "*.png")

internal val APP_RESOURCES_DIRECTORY = File("./app/src/main/resources").absoluteFile
internal val INITIAL_ICON_DIRECTORY = APP_RESOURCES_DIRECTORY.resolve("images/Classic")
internal val INITIAL_BACKGROUND_DIRECTORY = APP_RESOURCES_DIRECTORY.resolve("images/backgrounds")
internal val UNKNOWN_IMAGE = File("INV_Misc_QuestionMark.png")

internal fun chooseIconFromResources(prompt: String, initialDirectory: File): String? {
    val files = chooseFile(prompt, arrayOf(IMAGE_FILES_FILTER), initialDirectory)
    if (files.isEmpty()) return null
    val file = files[0]

    val inResourcesDir = file.canonicalPath.contains(APP_RESOURCES_DIRECTORY.canonicalPath + File.separator)
    if (!inResourcesDir) return null

    var path = file.relativeTo(APP_RESOURCES_DIRECTORY).path
    path = path.replace(File.separatorChar, '/')
    return path
}
