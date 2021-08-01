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

package org.hedbor.evan.classictalents.app.view

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import org.hedbor.evan.classictalents.app.model.MainViewModel
import org.hedbor.evan.classictalents.app.model.WowClassViewModel
import org.hedbor.evan.classictalents.app.view.styles.ClassStyles
import org.hedbor.evan.classictalents.app.view.styles.TalentStyles
import tornadofx.*

class MainView : View("Classic WoW Talent Calculator") {
    private val model by inject<MainViewModel>()

    private val fragments = observableMapOf<String, WowClassFragment>()

    init {
        model.onSetup()
    }

    override val root = vbox {
        addClass(ClassStyles.classBackground)
        // the nested hbox prevents the inner hbox from expanding to fill all horizontal space
        hbox {
            alignment = Pos.CENTER
            padding = insets(vertical = 20)
            hbox {
                addClass(ClassStyles.classFooter)
                padding = insets(vertical = 10, horizontal = 15)
                spacing = 30.0
                alignment = Pos.CENTER
                hgrow = Priority.NEVER

                for (wowClass in model.classes) {
                    val wowClassModel = WowClassViewModel(wowClass)
                    val scope = Scope(wowClassModel)
                    val fragment = find<WowClassFragment>(scope)
                    fragments += wowClass.translationKey to fragment

                    generateClassButton(wowClassModel)
                }
            }
        }
        hbox {
            alignment = Pos.CENTER
            for ((classKey, fragment) in fragments) {
                this += fragment.root.also {
                    val shouldBeEnabled = model.activeClassKey.isEqualTo(classKey)
                    it.disableWhen(shouldBeEnabled.not())
                    it.managedWhen(shouldBeEnabled)
                    it.visibleWhen(shouldBeEnabled)
                }
            }
        }
    }

    private fun Region.generateClassButton(wowClassModel: WowClassViewModel) = button {
        hgrow = Priority.NEVER
        addClass(TalentStyles.talentButton)
        graphic = StackPane().apply {
            imageview(wowClassModel.icon) {
                addClass(TalentStyles.talentIcon)
            }
            imageview(wowClassModel.borderImage) {
                addClass(TalentStyles.talentIcon)
            }
            imageview(wowClassModel.borderHiliteImage) {
                addClass(TalentStyles.talentIcon)
                visibleWhen(this@button.hoverProperty())
            }
            region {
                // i dont know why, but using `model.activeClassKey.isEqualTo`
                // caused the style class to never change
                // enjoy this ugly workaround instead
                fun addOrRemoveClass(newKey: String, classKey: String) {
                    if (newKey == classKey)
                        addClass(TalentStyles.maxedOutBorder)
                    else
                        removeClass(TalentStyles.maxedOutBorder)
                }
                addOrRemoveClass(model.activeClassKey.value, wowClassModel.translationKey.value)
                model.activeClassKey.onChange { newKey ->
                    addOrRemoveClass(newKey!!, wowClassModel.translationKey.value)
                }
            }
        }
        action { model.onClassButtonClicked(wowClassModel.translationKey) }
    }
}
