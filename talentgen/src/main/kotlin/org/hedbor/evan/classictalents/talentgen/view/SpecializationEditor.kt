package org.hedbor.evan.classictalents.talentgen.view

import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.OverrunStyle
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import org.hedbor.evan.talenttreegenerator.*
import org.hedbor.evan.talenttreegenerator.model.*
import tornadofx.*


class SpecializationEditor : Fragment() {
    val wowClassModel: WowClassModel by inject()
    val model: SpecializationModel by inject()

    init {
        titleProperty.bind(model.displayName)
        model.validated.bind(model.valid)
    }

    override val root = borderpane {
        center {
            form {
                fieldset("Specialization") {
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

                        label(wowClassModel.translationKey.stringBinding{ "$it." })
                        textfield(model.translationKey) {
                            isValidName(NameType.TRANSLATION_KEY)
                            enableWhen(useCustomTranslationKeyCheckBox.selectedProperty())
                        }
                    }
                    field("Background Image") {
                        invisibleCheckbox()
                        textfield(model.backgroundImage) {
                            requiredWithSuccess()
                        }
                        button("...") {
                            action {
                                val image = chooseIconFromResources("Choose a Background Image", INITIAL_BACKGROUND_DIRECTORY)
                                if (image != null)
                                    model.backgroundImage.value = image
                            }
                        }
                    }
                    field("Talents") {
                        invisibleCheckbox()
                        gridpane {
                            for (row in 0 until Specialization.ROWS) {
                                for (col in 0 until Specialization.COLUMNS) {
                                    talentbutton(row, col)
                                }
                            }
                        }
                    }
                }
            }
        }
        bottom {
            buttonbar {
                button("Apply", ButtonBar.ButtonData.APPLY) {
                    enableWhen(model.valid.and(model.dirty))
                    action { saveEditor() }
                }
                button("OK", ButtonBar.ButtonData.OK_DONE) {
                    enableWhen(model.valid)
                    action { saveAndCloseEditor() }
                }
                button("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE) {
                    action { closeEditorWithConfirmation() }
                }
            }
        }
    }

    private fun saveEditor() {
        model.commit()
        wowClassModel.markDirty(wowClassModel.specializations)
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
        model.rollback()
        close()
    }

    private fun closeEditorWithConfirmation() {
        if (!model.dirty.value) {
            closeEditor()
        } else {
            alert(Alert.AlertType.CONFIRMATION, "Discard changes?",
                "Are you sure you want to discard all changes made to this spec? You will not be able to reverse your decision.") {
                if (it.buttonData == ButtonBar.ButtonData.OK_DONE)
                    closeEditor()
            }
        }
    }

    private fun EventTarget.talentbutton(row: Int, col: Int): StackPane {
        val location = Location(row, col)
        return stackpane {
            gridpaneConstraints {
                columnRowIndex(col, row)
            }
            val editButton = button(getOrCreateTalentAt(location).displayNameProperty) {
                textOverrun = OverrunStyle.LEADING_ELLIPSIS
                prefWidth = 65.0
                prefHeight = 65.0
                action {
                    editTalent(getOrCreateTalentAt(location))
                }
            }
            anchorpane {
                isPickOnBounds = false
                button("X") {
                    anchorpaneConstraints {
                        topAnchor = 1.0
                        rightAnchor = 1.0
                    }
                    style {
                        fontWeight = FontWeight.BOLD
                    }
                    prefWidth = 25.0
                    prefHeight = 25.0
                    padding = Insets(1.0)
                    action {
                        resetTalent(location, editButton)
                    }
                }
            }
        }
    }

    private fun getOrCreateTalentAt(location: Location): Talent {
        var talent = model.talents.find { it.location == location  }
        if (talent == null) {
            talent = Talent(
                location = location,
                description = Talent.HELPFUL_DESCRIPTION)
            model.talents += talent
        }
        return talent
    }

    private fun editTalent(talent: Talent) {
        val talentModel = TalentModel(talent)
        val scope = Scope(wowClassModel, model, talentModel)
        find<TalentEditor>(scope).openModal()
    }

    /**
     * Resets the talent at the given location and rebinds the textProperty of its edit button.
     */
    private fun resetTalent(location: Location, editButton: Button) {
        alert(Alert.AlertType.CONFIRMATION, "Reset this talent?",
            "Are you sure you want to reset this talent? You will not be able to reverse your decision."
        ) {
            editButton.textProperty().unbind()
            model.talents.removeIf { it.location == location }
            val newTalent = getOrCreateTalentAt(location)
            editButton.textProperty().bind(newTalent.displayNameProperty)
        }
    }
}