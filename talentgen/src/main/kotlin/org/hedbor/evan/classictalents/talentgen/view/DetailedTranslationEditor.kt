package org.hedbor.evan.classictalents.talentgen.view

import javafx.scene.control.ButtonBar
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import org.hedbor.evan.classictalents.talentgen.model.BundleEntryModel
import tornadofx.*


class DetailedTranslationEditor : Fragment() {
    companion object {
        private val CHOICE_FORMAT_STRINGS = mapOf(
            2 to "{0,choice,1#AAAA|2#BBBB}",
            3 to "{0,choice,1#AAAA|2#BBBB|3#CCCC}",
            4 to "{0,choice,1#AAAA|2#BBBB|3#CCCC|4#DDDD}",
            5 to "{0,choice,1#AAAA|2#BBBB|3#CCCC|4#DDDD|5#EEEE}"
        )
    }

    private val model by inject<BundleEntryModel>()
    private lateinit var displayNameTextArea: TextArea

    init {
        title = messages["editor.title.translation.detailed"]
    }

    override val root = borderpane {
        center {
            val f = form {
                fieldset(messages.format("editor.fieldset.edit_translation", model.translationKey.value.orEmpty())) {
                    field(messages["editor.field.insert_choice"]) {
                        button(messages["action.editor.insert_choice.rank2"]).action { insertChoiceFormat(2) }
                        button(messages["action.editor.insert_choice.rank3"]).action { insertChoiceFormat(3) }
                        button(messages["action.editor.insert_choice.rank4"]).action { insertChoiceFormat(4) }
                        button(messages["action.editor.insert_choice.rank5"]).action { insertChoiceFormat(5) }
                    }
                    field(messages["editor.field.display_name"]) {
                        displayNameTextArea = textarea(model.displayName) {
                            isWrapText = true
                        }
                    }
                }
            }
            BorderPane.setMargin(f, insets(5.0, 5.0, 0.0, 5.0))
        }
        bottom {
            val bb = buttonbar {
                button(messages["action.editor.apply"], ButtonBar.ButtonData.APPLY) {
                    enableWhen(model.dirty)
                    action {
                        model.commit()
                    }
                }
                button(messages["action.editor.ok"], ButtonBar.ButtonData.OK_DONE) {
                    action {
                        model.commit()
                        close()
                    }
                }
                button(messages["action.editor.cancel"], ButtonBar.ButtonData.CANCEL_CLOSE) {
                    action {
                        model.rollback()
                        close()
                    }
                }
            }
            BorderPane.setMargin(bb, insets(5.0))
        }
    }

    private fun insertChoiceFormat(rank: Int) {
        require(rank in 2..5)
        val caretPos = displayNameTextArea.caretPosition
        displayNameTextArea.insertText(caretPos, CHOICE_FORMAT_STRINGS[rank])
    }
}