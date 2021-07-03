package org.hedbor.evan.classictalents.talentgen.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.util.converter.NumberStringConverter
import org.hedbor.evan.classictalents.common.model.*
import org.hedbor.evan.classictalents.talentgen.INITIAL_ICON_DIRECTORY
import org.hedbor.evan.classictalents.talentgen.chooseIconFromResources
import org.hedbor.evan.classictalents.talentgen.formatTranslationKey
import tornadofx.*


class TalentEditor : Fragment("Talent Editor") {
    companion object {
        // calculated as 1/value
        private const val SMALL_TEXTBOX_SCALE_FACTOR = 5
        private const val COMBOBOX_SCALE_FACTOR = 6
    }

    private val wowClassModel: WowClassModel by inject()
    private val specModel: SpecializationModel by inject()
    private val model: TalentModel by inject()

    private val isSpellProperty = SimpleBooleanProperty()

    init {
        model.translationKey.bind(model.displayName.stringBinding { formatTranslationKey(it) })
    }

    override val root = borderpane {
        center {
            vbox {
                addGeneralInfo()
                squeezebox {
                    fold("Spell Info") {
                        isAnimated = false
                        heightProperty().addListener { _, _, _ ->
                            currentStage?.sizeToScene()
                        }
                        addSpellInfo()
                    }
                }
            }
        }
        bottom {
            addButtonBar()
        }
        children.forEach {
            BorderPane.setMargin(it, Insets(0.0, 5.0, 5.0, 5.0))
        }
    }

    private fun Region.addGeneralInfo() {
        form {
            fieldset("General") {
                field("Display Name") {
                    textfield(model.displayName) { mustBePresent() }
                }
                field("Translation Key") {
                    label(wowClassModel.translationKey.stringBinding(specModel.translationKey) {
                        "$it.${specModel.translationKey.value}."
                    })
                    textfield(model.translationKey) { isDisable = true }
                }
                field("Icon") {
                    textfield(model.icon) { mustBePresent() }
                    button("...") {
                        action {
                            val icon = chooseIconFromResources("Choose an icon", INITIAL_ICON_DIRECTORY)
                            if (icon != null)
                                model.icon.value = icon
                        }
                    }
                }
                field("Max Rank") {
                    combobox(model.maxRank, (Talent.MINIMUM_RANK..Talent.MAXIMUM_PERMISSIBLE_RANK).toList()) {
                        if (selectionModel.selectedItem !in items) {
                            selectionModel.selectLast()
                        }
                        prefWidthProperty().bind(this@field.widthProperty().divide(COMBOBOX_SCALE_FACTOR))
                        maxWidthProperty().bind(prefWidthProperty())
                    }
                }
                field("Prerequisite") {
                    label("Row")
                    textfield(model.prerequisite.select { it.rowProperty }, NumberStringConverter()) {
                        prefWidthProperty().bind(this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR))
                        maxWidthProperty().bind(prefWidthProperty())
                        model.validationContext.mustBeInRange(
                            this,
                            0 until wowClassModel.era.value.talentRowCount
                        )
                    }
                    label("Col")
                    textfield(model.prerequisite.select { it.columnProperty }, NumberStringConverter()) {
                        prefWidthProperty().bind(this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR))
                        maxWidthProperty().bind(prefWidthProperty())
                        model.validationContext.mustBeInRange(this, 0 until Specialization.TALENT_COLUMN_COUNT)
                    }
                }
                field("Description") {
                    textarea(model.description) {
                        isWrapText = true
                        prefRowCount = 5
                        prefColumnCount = 28
                        mustBePresent()
                    }
                }
            }
        }
    }

    private fun Region.addSpellInfo() {
        form {
            fieldset("Spell Info") {
                field("Is spell") {
                    checkbox(property = isSpellProperty)
                }
                field("Resource") {
                    textfield(model.spell.select { it.resourceCostProperty }, NumberStringConverter()) {
                        prefWidthProperty().bind(
                            this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR)
                        )
                        maxWidthProperty().bind(prefWidthProperty())
                        enableWhen(isSpellProperty)
                        tooltip("A resource cost of 0 indicates that this spell has no resource cost at all.")
                        model.validationContext.mustBeNonNegative<Int>(this)
                    }
                    combobox(model.spell.select { it.resourceTypeProperty }, ResourceType.values().toList()) {
                        prefWidthProperty().bind(
                            this@field.widthProperty().divide(COMBOBOX_SCALE_FACTOR)
                        )
                        maxWidthProperty().bind(prefWidthProperty())
                        enableWhen(isSpellProperty)
                        minWidth = 50.0
                        if (selectionModel.selectedItem !in items) {
                            selectionModel.selectFirst()
                        }
                    }
                }
                field("Cast Time") {
                    textfield(model.spell.select { it.castTimeProperty }, NumberStringConverter()) {
                        prefWidthProperty().bind(
                            this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR)
                        )
                        maxWidthProperty().bind(prefWidthProperty())
                        enableWhen(isSpellProperty)
                        tooltip("A cast time of 0 sec indicates that the spell is instant cast.")
                        model.validationContext.mustBeNonNegative<Double>(this)
                    }
                    label("sec") {
                        enableWhen(isSpellProperty)
                    }
                }
                field("Cooldown") {
                    textfield(model.spell.select { it.cooldownProperty }, NumberStringConverter()) {
                        prefWidthProperty().bind(
                            this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR)
                        )
                        maxWidthProperty().bind(prefWidthProperty())
                        enableWhen(isSpellProperty)
                        tooltip("A cooldown of 0 indicates that this spell has no cooldown period.")
                        model.validationContext.mustBeNonNegative<Double>(this)
                    }
                    combobox(model.spell.select { it.cooldownUnitProperty }, CooldownUnit.values().toList()) {
                        prefWidthProperty().bind(
                            this@field.widthProperty().divide(COMBOBOX_SCALE_FACTOR)
                        )
                        maxWidthProperty().bind(prefWidthProperty())
                        enableWhen(isSpellProperty)
                        if (selectionModel.selectedItem !in items) {
                            selectionModel.selectFirst()
                        }
                    }
                }
                field("Range") {
                    textfield(model.spell.select { it.rangeProperty }, NumberStringConverter()) {
                        prefWidthProperty().bind(
                            this@field.widthProperty().divide(SMALL_TEXTBOX_SCALE_FACTOR)
                        )
                        maxWidthProperty().bind(prefWidthProperty())
                        enableWhen(isSpellProperty)
                        tooltip("A range of 0 yd indicates self-range, and a range of 5 yd indicates melee range.")
                        model.validationContext.mustBeNonNegative<Double>(this)
                    }
                    label("yd") {
                        enableWhen(isSpellProperty)
                    }
                }
            }
        }
    }

    private fun Region.addButtonBar() {
        buttonbar {
            button("Apply", ButtonBar.ButtonData.APPLY) {
                enableWhen(model.dirty.and(model.valid))
                action { saveEditor() }
            }
            button("OK", ButtonBar.ButtonData.OK_DONE) {
                enableWhen(model.valid)
                action { saveAndCloseEditor() }
            }
            button("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE) {
                action { closeEditorWithWarning() }
            }
        }
    }

    private fun saveEditor() {
        model.commit()
        specModel.markDirty(specModel.talents)
    }

    private fun saveAndCloseEditor() {
        model.translationKey.unbind()
        saveEditor()
        close()
    }

    private fun closeEditor() {
        model.translationKey.unbind()
        model.rollback()
        close()
    }

    private fun closeEditorWithWarning() {
        if (!model.dirty.value) {
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
}