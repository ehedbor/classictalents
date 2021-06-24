package org.hedbor.evan.talenttreegenerator

import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import javafx.scene.control.TextInputControl
import javafx.stage.FileChooser
import tornadofx.checkbox
import tornadofx.chooseFile
import tornadofx.stringBinding
import tornadofx.validator
import java.io.File


/**
 * Create an invisible checkbox in order to align "required" fields with "optional" fields.
 */
internal fun EventTarget.invisibleCheckbox() {
    checkbox {
        isVisible = false
    }
}


internal fun bindTranslationKey(translationKey: SimpleStringProperty, displayName: SimpleStringProperty) {
    fun formatKey(display: String?): String {
        return display?.lowercase()?.replace(' ', '_') ?: ""
    }

    translationKey.bind(displayName.stringBinding { formatKey(it) })
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


internal enum class NameType(val pattern: Regex, val errorMsg: String) {
    DISPLAY_NAME(
        Regex("^[A-Za-z0-9 ]*$"),
        "Display name may only contain letters, numbers and spaces."),
    TRANSLATION_KEY(
        Regex("^[a-z0-9_]*$"),
        "Translation key may only contain lowercase letters, numbers, and underscores."),

}

internal fun TextInputControl.isValidName(nameType: NameType) {
    validator {
        val text = text
        when {
            text.isNullOrEmpty() -> error("This field is required.")
            !(text matches nameType.pattern) -> error(nameType.errorMsg)
            else -> success()
        }
    }
}

/**
 * The default [tornadofx.required] method returns null when validation is successful.
 * This method returns [tornadofx.ValidationContext.success] in such a situation.
 */
internal fun TextInputControl.requiredWithSuccess() {
    validator {
        if (text.isNullOrEmpty()) error("This field is required.") else success()
    }
}
