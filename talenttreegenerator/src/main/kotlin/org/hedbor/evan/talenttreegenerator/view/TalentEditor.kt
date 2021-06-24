package org.hedbor.evan.talenttreegenerator.view

import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.util.converter.NumberStringConverter
import org.hedbor.evan.talenttreegenerator.*
import org.hedbor.evan.talenttreegenerator.model.Specialization
import org.hedbor.evan.talenttreegenerator.model.SpecializationModel
import org.hedbor.evan.talenttreegenerator.model.TalentModel
import org.hedbor.evan.talenttreegenerator.model.WowClassModel
import tornadofx.*


class TalentEditor : Fragment("Talent Editor") {
    companion object {
        // calculated as 1/value
        private const val SMALL_TEXTBOX_SCALE_FACTOR = 5
        private const val COMBOBOX_SCALE_FACTOR = 6
    }

    val wowClassModel: WowClassModel by inject()
    val specModel: SpecializationModel by inject()
    val model: TalentModel by inject()

    init {
        model.validated.bind(modelValid)
    }

    override val root = form {
        fieldset("General") {
            field("Display Name") {
                invisibleCheckbox()
                textfield(model.displayName) {
                    isValidName(NameType.DISPLAY_NAME)
                }
            }
            field("Translation Key") {
                val useCustomTranslationKeyCheckBox = checkbox {
                    action {
                        if (!isSelected) {
                            bindTranslationKey(model.translationKey, model.displayName)
                        } else {
                            unbindTranslationKey(model.translationKey)
                        }
                    }

                }
                bindTranslationKey(model.translationKey, model.displayName)

                label(wowClassModel.translationKey.stringBinding(specModel.translationKey) {
                    "$it.${specModel.translationKey.value}."
                })
                textfield(model.translationKey) {
                    isValidName(NameType.TRANSLATION_KEY)
                    enableWhen(useCustomTranslationKeyCheckBox.selectedProperty())
                }
            }
            field("Icon") {
                invisibleCheckbox()
                textfield(model.icon) {
                    requiredWithSuccess()
                }
                button("...") {
                    action {
                        val icon = chooseIconFromResources("Choose an icon", INITIAL_ICON_DIRECTORY)
                        if (icon != null)
                            model.icon.value = icon
                    }
                }
            }
            field("Max Rank") {
                invisibleCheckbox()
                combobox(model.maxRank, listOf(1, 2, 3, 4, 5)) {
                    if (selectionModel.selectedItem !in items) {
                        selectionModel.selectLast()
                    }
                    prefWidthProperty().bind(this@field.widthProperty().divide(COMBOBOX_SCALE_FACTOR))
                    maxWidthProperty().bind(prefWidthProperty())
                    validator {
                        if (value !in items)
                            error("Must select a rank.")
                        else
                            success()
                    }
                }
            }
            field("Prerequisite") {
                checkbox(property = model.hasPrerequisite)
                label("Row")
                textfield(model.prerequisite.row, NumberStringConverter()) {
                    prefWidthProperty().bind(this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR))
                    maxWidthProperty().bind(prefWidthProperty())
                    enableWhen(model.hasPrerequisite)
                    validator {
                        val text = text
                        when {
                            isDisable -> null
                            text.isNullOrEmpty() -> error("This field is required.")
                            text.toIntOrNull() !in 0 until Specialization.ROWS -> error("Row out of bounds.")
                            else -> success()
                        }
                    }
                }
                label("Col")
                textfield(model.prerequisite.column, NumberStringConverter()) {
                    prefWidthProperty().bind(this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR))
                    maxWidthProperty().bind(prefWidthProperty())
                    enableWhen(model.hasPrerequisite)
                    validator {
                        val text = text
                        when {
                            isDisable -> null
                            text.isNullOrEmpty() -> error("This field is required.")
                            text.toIntOrNull() !in 0 until Specialization.ROWS -> error("Column out of bounds.")
                            else -> success()
                        }
                    }
                }
            }
            field("Description *") {
                invisibleCheckbox()
                textarea(model.description) {
                    isWrapText = true
                    prefRowCount = 5
                    prefColumnCount = 28
                    requiredWithSuccess()
                }
            }
        }
        fieldset("Spell Info") {
            field("Is spell") {
                checkbox(property = model.isSpell)
            }
            field("Resource") {
                enableWhen(model.isSpell)
                checkbox(property = model.spell.hasResource) {
                    tooltip("Has resource?")
                }
                textfield(model.spell.resourceCost, NumberStringConverter()) {
                    prefWidthProperty().bind(this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR))
                    maxWidthProperty().bind(prefWidthProperty())
                    enableWhen(model.spell.hasResource)
                    validator {
                        val text = text
                        when {
                            isDisable -> null
                            text.isNullOrEmpty() -> error("This field is required.")
                            else -> {
                                val integerValue = text.toIntOrNull()
                                if (integerValue != null && integerValue < 0)
                                    error("Resource cost must not be negative.")
                                success()
                            }
                        }
                    }
                }
                combobox(model.spell.resourceType, listOf("mana", "% of base mana", "energy", "rage")) {
                    prefWidthProperty().bind(this@field.widthProperty().divide(COMBOBOX_SCALE_FACTOR))
                    maxWidthProperty().bind(prefWidthProperty())
                    minWidth = 50.0
                    if (selectionModel.selectedItem !in items) {
                        selectionModel.selectFirst()
                    }
                    enableWhen(model.spell.hasResource)
                    validator {
                        when {
                            isDisable -> null
                            value.isNullOrEmpty() || value !in items -> error("This field is required.")
                            else -> success()
                        }
                    }
                }
            }
            field("Cast Time") {
                enableWhen(model.isSpell)
                checkbox(property = model.spell.isNotInstantCast) {
                    tooltip("Has cast time?")
                }
                textfield(model.spell.castTime, NumberStringConverter()) {
                    prefWidthProperty().bind(this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR))
                    maxWidthProperty().bind(prefWidthProperty())
                    enableWhen(model.spell.isNotInstantCast)
                    validator {
                        val text = text
                        when {
                            isDisable -> null
                            text.isNullOrEmpty() -> error("This field is required.")
                            else -> {
                                val integerValue = text.toDoubleOrNull()
                                if (integerValue != null && integerValue < 0)
                                    error("Cast time must not be negative.")
                                success()
                            }
                        }
                    }
                }
                label("sec") {
                    enableWhen(model.spell.isNotInstantCast)
                }
            }
            field("Cooldown") {
                enableWhen(model.isSpell)
                checkbox(property = model.spell.hasCooldown) {
                    tooltip("Has cooldown?")
                }
                textfield(model.spell.cooldown, NumberStringConverter()) {
                    prefWidthProperty().bind(this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR))
                    maxWidthProperty().bind(prefWidthProperty())
                    enableWhen(model.spell.hasCooldown)
                    requiredWhen(disableProperty().not())
                    validator {
                        val text = text
                        when {
                            isDisable -> null
                            text.isNullOrEmpty() -> error("This field is required.")
                            else -> {
                                val integerValue = text.toDoubleOrNull()
                                if (integerValue != null && integerValue <= 0)
                                    error("Cooldown must be positive.")
                                success()
                            }
                        }
                    }
                }
                combobox(model.spell.cooldownUnit, listOf("sec", "min", "hr")) {
                    prefWidthProperty().bind(this@field.widthProperty().divide(COMBOBOX_SCALE_FACTOR))
                    maxWidthProperty().bind(prefWidthProperty())
                    if (selectionModel.selectedItem !in items) {
                        selectionModel.selectFirst()
                    }
                    enableWhen(model.spell.hasCooldown)
                    validator {
                        when {
                            isDisable -> null
                            value.isNullOrEmpty() || value !in items -> error("This field is required.")
                            else -> success()
                        }
                    }
                }
            }
            field("Range") {
                enableWhen(model.isSpell)
                checkbox(property = model.spell.hasRange) {
                    tooltip("Is self cast?")
                }
                textfield(model.spell.range, NumberStringConverter()) {
                    prefWidthProperty().bind(this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR))
                    maxWidthProperty().bind(prefWidthProperty())
                    tooltip("A range of 0 indicates melee range.")
                    enableWhen(model.spell.hasRange)
                    requiredWhen(disableProperty().not())
                    validator {
                        val text = text
                        when {
                            isDisable -> null
                            text.isNullOrEmpty() -> error("This field is required.")
                            else -> {
                                val integerValue = text.toIntOrNull()
                                if (integerValue != null && integerValue <= 0)
                                    error("Range must be positive.")
                                success()
                            }
                        }
                    }
                }
                label("yd") {
                    enableWhen(model.spell.hasRange)
                }
            }
        }
        buttonbar {
            button("Apply", ButtonBar.ButtonData.APPLY) {
                enableWhen(modelDirty.and(modelValid))
                action { saveEditor() }
            }
            button("OK", ButtonBar.ButtonData.OK_DONE) {
                enableWhen(modelValid)
                action { saveAndCloseEditor() }
            }
            button("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE) {
                action { closeEditorWithWarning() }
            }
        }
    }

    private fun saveEditor() {
        model.location.commit()
        model.prerequisite.commit()
        model.spell.commit()
        model.commit()
        specModel.markDirty(specModel.talents)
    }

    private fun saveAndCloseEditor() {
        unbindTranslationKey(model.translationKey)
        model.validated.unbind()
        saveEditor()
        close()
    }

    private fun closeEditor() {
        unbindTranslationKey(model.translationKey)
        model.validated.unbind()
        model.location.rollback()
        model.prerequisite.rollback()
        model.spell.rollback()
        model.rollback()
        close()
    }

    private fun closeEditorWithWarning() {
        if (!modelDirty.value) {
            closeEditor()
        } else {
            alert(
                Alert.AlertType.CONFIRMATION,
                "Discard changes?",
                "Are you sure you want to discard all changes made to this talent? " +
                        "You will not be able to reverse your decision."
            ) {
                if (it.buttonData == ButtonBar.ButtonData.OK_DONE)
                    closeEditor()
            }
        }
    }

    private val modelDirty
        get() = model.dirty
            .or(model.location.dirty)
            .or(model.prerequisite.dirty)
            .or(model.spell.dirty)
    
    private val modelValid
        get() = model.valid
            .and(model.location.valid)
            .and(model.prerequisite.valid)
            .and(model.spell.valid)
}