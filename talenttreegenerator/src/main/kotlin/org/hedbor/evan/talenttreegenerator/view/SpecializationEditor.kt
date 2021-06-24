package org.hedbor.evan.talenttreegenerator.view

import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import org.hedbor.evan.talenttreegenerator.*
import org.hedbor.evan.talenttreegenerator.model.*
import tornadofx.*


class SpecializationEditor : Fragment() {
    val wowClassModel: WowClassModel by inject()
    val model: SpecializationModel by inject()

    private val talentButtons: ObservableList<ObservableList<Button>> = observableListOf()

    init {
        titleProperty.bind(model.displayName)
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
                                val talentRow = observableListOf<Button>()
                                for (col in 0 until Specialization.COLUMNS) {
                                    talentRow += talentbutton(row, col)
                                }
                                talentButtons += talentRow
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

    private fun EventTarget.talentbutton(row: Int, col: Int): Button {
        val location = Location(row, col)
        var talent = model.talents.find { it.location == location  }
        if (talent == null) {
            talent = Talent(
                location = location,
                description = "{0,choice,1#ONE_POINT|2#TWO_POINTS|3#THREE_POINTS|4#FOUR_POINTS|5#FIVE_POINTS}")
            model.talents += talent
        }

        return button("X") {
            gridpaneConstraints {
                columnRowIndex(col, row)
            }
            prefWidth = 50.0
            prefHeight = 50.0

            textProperty().bind(talent.displayNameProperty)

            action {
                editTalent(talent)
            }
        }
    }

    private fun saveEditor() {
        model.commit()
    }

    private fun saveAndCloseEditor() {
        unbindTranslationKey(model.translationKey)
        saveEditor()
        close()
    }

    private fun closeEditor() {
        unbindTranslationKey(model.translationKey)
        model.rollback()
        close()
    }

    private fun closeEditorWithConfirmation() {
        if (!model.dirty.value) {
            closeEditor()
        } else {
            alert(
                Alert.AlertType.CONFIRMATION,
                "Discard changes?",
                "Are you sure you want to discard all changes made to this spec? You will not be able to reverse your decision."
            ) {
                if (it.buttonData == ButtonBar.ButtonData.OK_DONE)
                    closeEditor()
            }
        }
    }

    private fun editTalent(talent: Talent) {
        val talentModel = TalentModel(talent)
        val scope = Scope(wowClassModel, model, talentModel)
        find<TalentEditor>(scope).openModal()
    }
}