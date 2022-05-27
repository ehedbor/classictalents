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

package org.hedbor.evan.classictalents.app.view

import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import org.hedbor.evan.classictalents.app.view.styles.TalentStyles
import org.hedbor.evan.classictalents.app.viewmodel.TalentButtonViewModel
import tornadofx.*


class TalentButtonFragment : Fragment() {
    private val model by inject<TalentButtonViewModel>()

    override val root = stackpane {
        addClass(TalentStyles.talentContainer)
        button {
            addClass(TalentStyles.talentButton)

            prefWidth = model.buttonWidth
            prefHeight = model.buttonHeight

            minWidth = prefWidth
            minHeight = prefHeight
            maxWidth = prefWidth
            maxHeight = prefHeight

            setOnMouseClicked(model::onMouseClicked)

            tooltip = model.tooltip
            graphic = StackPane().apply {
                imageview(model.backgroundImage) {
                    addClass(TalentStyles.talentIcon)
                }
                imageview(model.borderImage) {
                    addClass(TalentStyles.talentIcon)
                }
                imageview(model.borderHiliteImage) {
                    addClass(TalentStyles.talentIcon)
                    visibleWhen(this@button.hoverProperty())
                }
                region {
                    toggleClass(TalentStyles.activeBorder, model.useActiveBorder)
                    toggleClass(TalentStyles.maxedOutBorder, model.useMaxedOutBorder)
                }
            }
        }
        alignment = Pos.BOTTOM_RIGHT
        label(model.rankCounterText) {
            addClass(TalentStyles.rankCounter)
            isMouseTransparent = true
            enableWhen(model.useRankCounterText)
        }
    }
}