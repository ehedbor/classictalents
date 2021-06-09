package org.hedbor.evan.talenttreegenerator.view

import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.control.Button
import org.hedbor.evan.talenttreegenerator.*
import org.hedbor.evan.talenttreegenerator.model.*
import tornadofx.*


class SpecializationEditor : Fragment() {
    val wowClassModel: WowClassModel by inject()
    val model: SpecializationModel by inject()

    private val talentButtons: ObservableList<ObservableList<Button>> = observableListOf()

    override val root = borderpane {
        left {
            form {
                fieldset {
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
                                    bindTranslationKey(model.translationKey, model.displayName, wowClassModel.translationKey)
                                } else {
                                    unbindTranslationKey(model.translationKey)
                                }
                            }
                        }
                        bindTranslationKey(model.translationKey, model.displayName, wowClassModel.translationKey)

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
                    field("Background Image *") {
                        invisibleCheckbox()
                        textfield(model.backgroundImage).required()
                        button("...") {
                            action {
                                val image = chooseIconFromResources("Choose a Background Image", INITIAL_BACKGROUND_DIRECTORY)
                                if (image != null)
                                    model.backgroundImage.value = image
                            }
                        }
                    }
                }
            }
        }
        center {
            vbox {
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

    init {
        titleProperty.bind(model.displayName)
    }

    private fun EventTarget.talentbutton(row: Int, col: Int): Button {
        val location = Location(row, col)
        var talent = model.talents.find { it.location == location  }
        if (talent == null) {
            talent = Talent(location = location, description = "{0,choice,1#AAAA|2#BBBB|3#CCCC|4#DDDD|5#EEEE}")
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

    private fun editTalent(talent: Talent) {
        val editScope = Scope()
        val talentModel = TalentModel()
        talentModel.item = talent
        setInScope(wowClassModel, editScope)
        setInScope(model, editScope)
        setInScope(talentModel, editScope)
        find<TalentEditor>(editScope).openModal()
    }
}