package org.hedbor.evan.talenttreegenerator.view

import javafx.scene.control.ButtonBar
import javafx.util.converter.NumberStringConverter
import org.hedbor.evan.talenttreegenerator.*
import org.hedbor.evan.talenttreegenerator.model.Specialization
import org.hedbor.evan.talenttreegenerator.model.SpecializationModel
import org.hedbor.evan.talenttreegenerator.model.TalentModel
import tornadofx.*


class TalentEditor : Fragment("Talent Editor") {
    companion object {
        // calculated as 1/value
        private const val SMALL_TEXTBOX_SCALE_FACTOR = 5
        private const val COMBOBOX_SCALE_FACTOR = 6
    }

    val specModel: SpecializationModel by inject()
    val model: TalentModel by inject()

    override val root = form {
        fieldset("Edit Talent") {
            field("Display Name *") {
                invisibleCheckbox()
                textfield(model.displayName) {
                    validator {
                        val text = text
                        when {
                            text.isNullOrEmpty() -> error("This field is required.")
                            !isValidDisplayName(text) -> error("Display name may only contain letters, numbers and spaces.")
                            else -> success()
                        }
                    }
                }
            }
            field("Translation Key *") {
                val useCustomTranslationKeyCheckBox = checkbox {
                    action {
                        if (!isSelected) {
                            bindTranslationKey(model.translationKey, model.displayName, specModel.translationKey)
                        } else {
                            unbindTranslationKey(model.translationKey)
                        }
                    }

                }
                bindTranslationKey(model.translationKey, model.displayName, specModel.translationKey)

                textfield(model.translationKey) {
                    validator {
                        val text = text
                        when {
                            text.isNullOrEmpty() -> error("This field is required.")
                            !isValidTranslationKey(text) ->
                                error("Translation key may only contain lowercase letters, numbers, periods and underscores.")
                            else -> success()
                        }
                    }
                    enableWhen(useCustomTranslationKeyCheckBox.selectedProperty())
                }
            }
            field("Icon *") {
                invisibleCheckbox()
                textfield(model.icon) {
                    validator {
                        when {
                            text.isNullOrEmpty() -> error("This field is required.")
                            else -> success()
                        }
                    }
                }
                button("...") {
                    action {
                        val icon = chooseIconFromResources("Choose an icon", INITIAL_ICON_DIRECTORY)
                        if (icon != null)
                            model.icon.value = icon
                    }
                }
            }
            field("Max Rank *") {
                invisibleCheckbox()
                combobox(model.maxRank, listOf(1, 2, 3, 4, 5)) {
                    selectionModel.selectFirst()
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
            field("Location") {
                invisibleCheckbox()
                label("Row")
                textfield(model.location.row, NumberStringConverter()) {
                    prefWidthProperty().bind(this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR))
                    maxWidthProperty().bind(prefWidthProperty())
                    isDisable = true
                }
                label("Col")
                textfield(model.location.column, NumberStringConverter()) {
                    prefWidthProperty().bind(this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR))
                    maxWidthProperty().bind(prefWidthProperty())
                    isDisable = true
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
                            isDisable -> success()
                            text.isNullOrEmpty() -> error("This field is required.")
                            text.toIntOrNull() !in 1..Specialization.ROWS -> error("Row out of bounds.")
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
                            isDisable -> success()
                            text.isNullOrEmpty() -> error("This field is required.")
                            text.toIntOrNull() !in 1..Specialization.ROWS -> error("Column out of bounds.")
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
                    required()
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
                            isDisable -> success()
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
                    selectionModel.selectFirst()
                    enableWhen(model.spell.hasResource)
                    validator {
                        when {
                            isDisable -> success()
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
                            isDisable -> success()
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
                            isDisable -> success()
                            text.isNullOrEmpty() -> error("This field is required.")
                            else -> {
                                val integerValue = text.toIntOrNull()
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
                    selectionModel.selectFirst()
                    enableWhen(model.spell.hasCooldown)
                    validator {
                        when {
                            isDisable -> success()
                            value.isNullOrEmpty() || value !in items -> error("This field is required.")
                            else -> success()
                        }
                    }
                }
            }
            field("Range") {
                enableWhen(model.isSpell)
                checkbox(property = model.spell.isNotMeleeRange) {
                    tooltip("Is ranged spell?")
                }
                textfield(model.spell.range, NumberStringConverter()) {
                    prefWidthProperty().bind(this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR))
                    maxWidthProperty().bind(prefWidthProperty())
                    enableWhen(model.spell.isNotMeleeRange)
                    requiredWhen(disableProperty().not())
                    validator {
                        val text = text
                        when {
                            isDisable -> success()
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
                    enableWhen(model.spell.isNotMeleeRange)
                }
            }
        }
        buttonbar {
            button("OK", ButtonBar.ButtonData.OK_DONE) {
                enableWhen(model.valid)
                action {
                    unbindTranslationKey(model.translationKey)
                    model.location.commit()
                    model.prerequisite.commit()
                    model.spell.commit()
                    model.commit()
                    close()
                }
            }
            button("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE) {
                action {
                    unbindTranslationKey(model.translationKey)
                    model.location.rollback()
                    model.prerequisite.rollback()
                    model.spell.rollback()
                    model.rollback()
                    close()
                }
            }
        }
    }
}