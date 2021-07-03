package org.hedbor.evan.classictalents.talentgen

import javafx.stage.FileChooser
import tornadofx.chooseFile
import java.io.File

internal fun formatTranslationKey(displayName: String?): String {
    return displayName?.lowercase()?.replace(' ', '_').orEmpty()
}


private val IMAGE_FILES_FILTER = FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.gif", "*.jpg", "*.png")
private val RESOURCES_DIRECTORY = File("./src/main/resources").absoluteFile
internal val INITIAL_ICON_DIRECTORY = RESOURCES_DIRECTORY.resolve("images/Classic")
internal val INITIAL_BACKGROUND_DIRECTORY = RESOURCES_DIRECTORY.resolve("images/backgrounds")

internal fun chooseIconFromResources(prompt: String, initialDirectory: File): String? {
    val files = chooseFile(prompt, arrayOf(IMAGE_FILES_FILTER), initialDirectory)
    if (files.isEmpty()) return null
    val file = files[0]

    val inResourcesDir = file.canonicalPath.contains(RESOURCES_DIRECTORY.canonicalPath + File.separator)
    if (!inResourcesDir) return null

    var path = file.relativeTo(RESOURCES_DIRECTORY).path
    path = '/' + path.replace(File.separatorChar, '/')
    return path
}
