package org.hedbor.evan.classictalents.talentgen.view

import javafx.beans.binding.BooleanExpression
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import org.hedbor.evan.classictalents.common.model.Era
import org.hedbor.evan.classictalents.common.model.Specialization
import org.hedbor.evan.classictalents.common.model.SpecializationModel
import org.hedbor.evan.classictalents.common.model.WowClassModel
import org.hedbor.evan.classictalents.talentgen.controller.TalentGenController
import org.hedbor.evan.classictalents.talentgen.formatTranslationKey
import tornadofx.*


class WowClassEditor : View("Talent Tree Editor") {
    private val controller: TalentGenController by inject()
    private val model: WowClassModel by lazy { controller.model }

    private lateinit var specFieldset: Fieldset

    init {
        titleProperty.bind(model.displayName)
        model.translationKey.bind(model.displayName.stringBinding { formatTranslationKey(it) })
    }

    override val root = borderpane {
        center {
            form {
                fieldset("General") {
                    field("Display Name") {
                        textfield(model.displayName) { mustBePresent() }
                    }
                    field("Translation Key") {
                        textfield(model.translationKey) { isDisable = true }
                    }
                    field("Era") {
                        combobox(model.era, Era.values().toList()) {
                            if (selectedItem !in items) {
                                selectionModel.selectFirst()
                            }
                        }
                    }
                }
                specFieldset = fieldset("Specializations") {
                    button("Add Specialization") {
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
                // you know that a spec is valid if the icon name is not null or empty,
                // since you cant close the spec editor otherwise
                model.isValid && specs!!.all { !it.backgroundImageProperty.value.isNullOrBlank() }
            }
        }

    private fun load() {
        model.translationKey.unbind()
        val success = controller.load()
        if (success) {
            // remove the old fields and replace them with new ones
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
        currentStage?.sizeToScene()
    }

    private fun addNewSpec(): Specialization {
        val index = model.specializations.size
        val spec = Specialization(displayName = "Spec ${index + 1}")
        model.specializations += spec
        return spec
    }

    private fun removeSpec(spec: Specialization) {
        model.specializations.remove(spec)
        currentStage?.sizeToScene()
    }

    private fun editSpec(spec: Specialization) {
        val specModel = SpecializationModel(spec)
        val scope = Scope(model, specModel)
        find<SpecializationEditor>(scope).openModal()
    }
}