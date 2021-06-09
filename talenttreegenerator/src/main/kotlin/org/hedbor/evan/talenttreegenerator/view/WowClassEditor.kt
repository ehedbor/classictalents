package org.hedbor.evan.talenttreegenerator.view

import org.hedbor.evan.talenttreegenerator.*
import org.hedbor.evan.talenttreegenerator.model.WowClassModel
import tornadofx.*


class WowClassEditor : Fragment() {
    val model: WowClassModel by inject()

    override val root = form {
        fieldset(title) {
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
                            bindTranslationKey(model.translationKey, model.displayName)
                        } else {
                            unbindTranslationKey(model.translationKey)
                        }
                    }
                }
                bindTranslationKey(model.translationKey, model.displayName)

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
        }
    }

    init {
        titleProperty.bind(model.displayName)
    }
}