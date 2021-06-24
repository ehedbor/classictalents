package org.hedbor.evan.talenttreegenerator.view

import javafx.beans.binding.BooleanExpression
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import org.hedbor.evan.talenttreegenerator.*
import org.hedbor.evan.talenttreegenerator.controller.TalentTreeController
import org.hedbor.evan.talenttreegenerator.model.Specialization
import org.hedbor.evan.talenttreegenerator.model.SpecializationModel
import org.hedbor.evan.talenttreegenerator.model.WowClassModel
import tornadofx.*


class WowClassEditor : View("Talent Tree Editor") {
    val controller: TalentTreeController by inject()
    val model: WowClassModel by lazy { controller.model }

    private lateinit var specFieldset: Fieldset

    init {
        titleProperty.bind(model.displayName)
    }

    override val root = borderpane {
        center {
            form {
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

                        textfield(model.translationKey) {
                            isValidName(NameType.TRANSLATION_KEY)
                            enableWhen(useCustomTranslationKeyCheckBox.selectedProperty())
                        }
                    }
                }
                specFieldset = fieldset("Specializations") {
                    button("Add Specialization") {
                        repeat(3) { createNewSpecField(addNewSpec()) }
                        action { createNewSpecField(addNewSpec()) }
                    }
                }
            }
        }
        bottom {
            buttonbar {
                button("Open") {
                    action { load() }
                }
                button("Save") {
                    enableWhen(model.dirty.and(modelValid))
                    action {
                        model.commit()
                        controller.save()
                    }
                }
                button("Save As") {
                    enableWhen(model.dirty.and(modelValid))
                    action {
                        model.commit()
                        controller.save(toNewFile = true)
                    }
                }
                button("Quit") {
                    action { quitWithConfirmation() }
                }
            }
        }
    }

    private val modelValid: BooleanExpression
        get() {
            return model.specializations.booleanBinding(model.valid) { specs ->
                model.isValid && specs!!.map { it.validated }.all { it }
            }
        }

    private fun load() {
        unbindTranslationKey(model.translationKey)
        val success = controller.load()
        if (success) {
            specFieldset.children.filterIsInstance<Field>().forEach { it.removeFromParent() }
            model.specializations.forEach { specFieldset.createNewSpecField(it) }
        }
    }

    private fun quitWithConfirmation() {
        if (!model.dirty.value) {
            model.commit()
            controller.quit()
        } else {
            alert(
                Alert.AlertType.CONFIRMATION,
                "Discard changes?",
                "Are you sure you want to discard all changes made to this class? You will not be able to reverse your decision."
            ) {
                if (it.buttonData == ButtonBar.ButtonData.OK_DONE) {
                    model.commit()
                    controller.quit()
                }
            }
        }
    }

    private fun Fieldset.createNewSpecField(spec: Specialization) {
        val field = Field().apply field@{
            textProperty.bind(spec.displayNameProperty)
            invisibleCheckbox()
            button("Edit") {
                action {
                    editSpec(spec)
                }
            }
            button("Delete") {
                action {
                    removeSpec(spec)
                    this@field.removeFromParent()
                }
            }
        }
        addChildIfPossible(field, model.specializations.size)
    }

    private fun addNewSpec(): Specialization {
        val index = model.specializations.size
        val spec = Specialization(displayName = "Spec ${index + 1}")
        model.specializations += spec
        return spec
    }

    private fun removeSpec(spec: Specialization) {
        model.specializations.remove(spec)
    }

    private fun editSpec(spec: Specialization) {
        val specModel = SpecializationModel(spec)
        val scope = Scope(model, specModel)
        find<SpecializationEditor>(scope).openModal()
    }
}