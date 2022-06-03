/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2022 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.talentgen.view

import javafx.beans.binding.BooleanExpression
import javafx.geometry.Insets
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import org.hedbor.evan.classictalents.common.model.Era
import org.hedbor.evan.classictalents.talentgen.INITIAL_ICON_DIRECTORY
import org.hedbor.evan.classictalents.talentgen.chooseIconFromResources
import org.hedbor.evan.classictalents.talentgen.controller.TalentGenController
import org.hedbor.evan.classictalents.talentgen.model.Specialization
import org.hedbor.evan.classictalents.talentgen.model.SpecializationModel
import org.hedbor.evan.classictalents.talentgen.model.WowClassModel
import tornadofx.*


class WowClassEditor : View() {
    private val controller: TalentGenController by inject()
    private val model: WowClassModel by lazy { controller.classModel }

    private lateinit var specFieldset: VBox

    private val modelValid: BooleanExpression
        get() {
            return model.specializations.booleanBinding(model.valid) { specs ->
                // you know that a spec is valid if the icon name is not null or empty,
                // since you cant close the spec editor otherwise
                model.isValid && specs!!.all { !it.backgroundImageProperty.value.isNullOrBlank() }
            }
        }

    init {
        titleProperty.bind(model.dirty.stringBinding { dirty ->
            val title = messages["editor.title.class"]
            if (dirty == true) "* $title" else title
        })
    }

    override val root = borderpane {
        top {
            addMenuBar()
        }
        center {
            addGeneralInfo()
        }
        children.forEach {
            BorderPane.setMargin(it, Insets(0.0, 5.0, 5.0, 5.0))
        }
    }

    private fun Region.addGeneralInfo() = form {
        fieldset(messages["editor.fieldset.general"]) {
            field(messages["editor.field.translation_key"]) {
                textfield(model.translationKey) {
                    mustBePresent()
                    mustBeValidKey()
                }
            }
            field(messages["editor.field.icon"]) {
                textfield(model.icon) {
                    mustBePresent()
                }
                button("...") {
                    action {
                        val image = chooseIconFromResources(messages["action.file.choose.icon"], INITIAL_ICON_DIRECTORY)
                        if (image != null) model.icon.value = image
                    }
                }
            }
            field(messages["editor.field.era"]) {
                combobox(model.era, Era.values().toList()) {
                    converter = object : StringConverter<Era>() {
                        override fun toString(`object`: Era?): String {
                            return if (`object` == null) "" else messages[`object`.translationKey]
                        }

                        override fun fromString(string: String?): Era {
                            throw IllegalStateException("unreachable")
                        }
                    }
                    if (selectedItem !in items) {
                        selectionModel.selectFirst()
                    }
                }
            }
        }
        fieldset(messages["editor.field.specs"]) {
            button(messages["action.editor.add_spec"]) {
                action { specFieldset.createNewSpecField(addNewSpec(), true) }
            }
            specFieldset = vbox {
                repeat(3) { createNewSpecField(addNewSpec(), false) }
            }
        }
    }

    private fun Region.addMenuBar() = menubar {
        menu(messages["menu.file"]) {
            item(messages["action.file.new"], "Ctrl+N") {
                action { controller.newDataFile() }
            }
            item(messages["action.file.open"], "Ctrl+O") {
                action { open() }
            }
            item(messages["action.file.save"], "Ctrl+S") {
                enableWhen(modelValid)
                action { controller.saveDataFile(false) }
            }
            item(messages["action.file.save_as"], "Ctrl+Shift+S") {
                action { controller.saveDataFile(true) }
            }
            separator()
            item(messages["action.quit"], "Ctrl+Q") {
                action { controller.quit() }
            }
        }
        menu(messages["menu.bundle"]) {
            item(messages["action.bundle.new"], "Ctrl+B") {
                action {
                    controller.newBundle()
                    editBundle()
                }
            }
            item(messages["action.bundle.open"], "Ctrl+Shift+B") {
                action {
                    val success = controller.openBundle()
                    if (success) editBundle()
                }
            }
        }
    }

    private fun open() {
        val success = controller.openDataFile()
        if (success) {
            // remove the old fields and replace them with new ones
            specFieldset.children.filterIsInstance<Field>().forEach { it.removeFromParent() }
            model.specializations
                .sortedBy { it.translationKey }
                .forEach { specFieldset.createNewSpecField(it, true) }
        }
    }

    private fun VBox.createNewSpecField(spec: Specialization, resize: Boolean) {
        val field = Field().apply field@{
            textProperty.bind(spec.translationKeyProperty)
            textProperty.addListener { _, _, _ ->
                // re-sort the children if the name changes
                specFieldset.children.sortBy { (it as? Field)?.text }
            }

            button(messages["action.editor.edit"]) {
                action {
                    editSpec(spec)
                }
            }
            button(messages["action.editor.delete"]) {
                action {
                    removeSpec(spec)
                    this@field.removeFromParent()
                }
            }
        }
        addChildIfPossible(field, model.specializations.size)
        if (resize) currentStage?.sizeToScene()
    }

    private fun addNewSpec(): Specialization {
        val index = model.specializations.size
        val spec = Specialization(translationKey = "spec${index + 1}")
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

    private fun editBundle() {
        val scope = Scope(controller, controller.bundleModel)
        find<TranslationEditor>(scope).openModal()
    }
}