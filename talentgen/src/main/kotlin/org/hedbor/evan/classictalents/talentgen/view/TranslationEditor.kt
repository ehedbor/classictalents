/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2021 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.talentgen.view

import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.util.StringConverter
import org.hedbor.evan.classictalents.talentgen.controller.TalentGenController
import org.hedbor.evan.classictalents.talentgen.model.BundleEntry
import org.hedbor.evan.classictalents.talentgen.model.BundleEntryModel
import org.hedbor.evan.classictalents.talentgen.model.BundleModel
import tornadofx.*
import java.util.*


class TranslationEditor : Fragment() {
    private val controller: TalentGenController by inject()
    private val model: BundleModel by lazy { controller.bundleModel }

    init {
        title = messages["editor.title.translation"]
    }

    override val root = borderpane {
        center {
            val form = addForm()
            BorderPane.setMargin(form, Insets(5.0, 5.0, 0.0, 5.0))
        }
        bottom {
            val buttonBar = addButtonBar()
            BorderPane.setMargin(buttonBar, Insets(5.0))
        }
    }

    private fun Region.addForm() = form {
        fieldset(messages["editor.fieldset.translation"], labelPosition = Orientation.VERTICAL) {
            field(messages["editor.field.locale"]) {
                textfield(model.locale, object : StringConverter<Locale>() {
                    override fun toString(`object`: Locale?): String {
                        return `object`?.toString().orEmpty()
                    }

                    override fun fromString(string: String?): Locale {
                        return if (string == null) Locale.ROOT else Locale.forLanguageTag(string)
                    }
                })
                button(messages["action.editor.set_locale.english"]) {
                    action {
                        model.locale.value  = Locale.ENGLISH
                    }
                }
                button(messages["action.editor.set_locale.root"]) {
                    action {
                        model.locale.value = Locale.ROOT
                    }
                }
            }
            field(messages["editor.field.bundle_entries"], orientation = Orientation.VERTICAL) {
                tableview(model.entries) {
                    prefWidth = 600.0
                    column(messages["editor.field.translation_key"], BundleEntry::translationKeyProperty) {
                        contentWidth(5.0, useAsMin = true)
                    }
                    column(messages["editor.field.display_name"], BundleEntry::displayNameProperty) {
                        contentWidth(20.0, useAsMin = true)
                        remainingWidth()
                        makeEditable()
                    }
                    smartResize()
                    contextmenu {
                        item(messages["action.editor.open_detailed_translation_editor"]) {
                            action {
                                if (selectedItem != null)
                                    openDetailedEditor(selectedItem!!)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Region.addButtonBar() = buttonbar {
        button(messages["action.file.save"]) {
            action {
                model.commit {
                    controller.saveBundle(false)
                }
            }
        }
        button(messages["action.file.save_as"]) {
            action {
                model.commit {
                    controller.saveBundle(true)
                }
            }
        }
        button(messages["action.editor.done"]) {
            action {
                if (!model.isDirty) {
                    model.commit()
                    close()
                } else {
                    confirm(
                        messages["confirm.discard"],
                        messages.format("confirm.discard.content", "this.bundle")
                    ) {
                        model.commit()
                        close()
                    }
                }
            }
        }
    }

    private fun openDetailedEditor(bundleEntry: BundleEntry) {
        val model = BundleEntryModel(bundleEntry)
        val scope = Scope(model)
        find<DetailedTranslationEditor>(scope).openModal()
    }
}