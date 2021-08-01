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
import org.hedbor.evan.classictalents.app.model.SpecializationViewModel
import org.hedbor.evan.classictalents.app.model.WowClassViewModel
import org.hedbor.evan.classictalents.app.view.styles.ClassStyles
import tornadofx.*


class WowClassFragment : Fragment() {
    private val model by inject<WowClassViewModel>()

    override val root = vbox {
        hbox {
            padding = insets(20)
            spacing = 10.0
            alignment = Pos.CENTER
            for (spec in model.specializations) {
                val specViewModel = SpecializationViewModel(model, spec)
                this += find<SpecializationFragment>(Scope(specViewModel)).also {
                    it.root.hgrow = Priority.NEVER
                }
            }
        }
        hbox {
            padding = insets(0, 10, 20,10)
            alignment = Pos.CENTER
            vbox {
                addClass(ClassStyles.classFooter)
                padding = insets(0, 10, 0, 10)
                alignment = Pos.CENTER
                label(model.talentInfoText) {
                    addClass(ClassStyles.classDescriptionText)
                    vgrow = Priority.NEVER
                }
                label(model.requiredLevelText) {
                    addClass(ClassStyles.classDescriptionText)
                    vgrow = Priority.NEVER
                }
                label(model.remainingPointsText) {
                    addClass(ClassStyles.classDescriptionText)
                    vgrow = Priority.NEVER
                }
            }
        }
    }
}