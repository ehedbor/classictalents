package org.hedbor.evan.talenttreegenerator

import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import javafx.stage.FileChooser
import tornadofx.checkbox
import tornadofx.chooseFile
import tornadofx.stringBinding
import java.io.File


/**
 * Create an invisible checkbox, for the purposes of alignment.
 */
internal fun EventTarget.invisibleCheckbox() {
    checkbox {
        isVisible = false
    }
}

internal fun bindTranslationKey(translationKey: SimpleStringProperty, displayName: SimpleStringProperty, rootKey: SimpleStringProperty? = null) {
    fun formatKey(display: String?): String {
        return display?.lowercase()?.replace(' ', '_') ?: ""
    }

    if (rootKey != null) {

        translationKey.bind(displayName.stringBinding(rootKey) { "${rootKey.value}.${formatKey(it)}" })
    } else {
        translationKey.bind(displayName.stringBinding { formatKey(it) })
    }
}

internal fun unbindTranslationKey(translationKey: SimpleStringProperty) {
    translationKey.unbind()
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

internal fun isValidDisplayName(str: String) = str.matches("^[A-Za-z0-9 ]*$".toRegex())

internal fun isValidTranslationKey(str: String) = str.matches("^[a-z0-9_.]*$".toRegex())