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

import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Tooltip
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import org.hedbor.evan.classictalents.app.view.styles.TalentTooltipStyles
import org.hedbor.evan.classictalents.app.viewmodel.TalentTooltipViewModel
import tornadofx.*


class TalentTooltip(private val model: TalentTooltipViewModel) : Tooltip() {
    companion object {
        private const val PREF_WIDTH = 400.0
        private const val PREF_HEIGHT = 600.0
    }

    init {
        isWrapText = true
        prefWidth = PREF_WIDTH
        maxHeight = PREF_HEIGHT

        graphic = VBox().apply {
            prefWidth = PREF_WIDTH
            minHeight = Region.USE_PREF_SIZE
            maxHeight = PREF_HEIGHT

            generateHeader()
            generateSpellInfo()
            generateDescription()
            generateFooter()
        }
    }

    private fun EventTarget.generateHeader() {
        label(model.talentName) {
            addClass(TalentTooltipStyles.tooltipTitle)
        }
        label(model.talentRank) {
            addClass(TalentTooltipStyles.tooltipSubtitle)
        }
    }

    private fun EventTarget.generateSpellInfo() {
        hbox {
            visibleAndManagedWhen(model.hasSpell)
            label(model.spellCostText) {
                addClass(TalentTooltipStyles.tooltipSubtitle)
                prefWidth = PREF_WIDTH / 2.0
                visibleAndManagedWhen(model.hasSpellCost)
            }
            label(model.spellRangeText) {
                addClass(TalentTooltipStyles.tooltipSubtitle)
                prefWidth = PREF_WIDTH / 2.0
                alignment = model.spellRangeAlignment
                visibleAndManagedWhen(model.hasSpellRange)
            }
        }
        hbox {
            visibleAndManagedWhen(model.hasSpell)
            label(model.spellCastTimeText) {
                addClass(TalentTooltipStyles.tooltipSubtitle)
                prefWidth = PREF_WIDTH / 2.0
                visibleAndManagedWhen(model.hasSpellCastTime)
            }
            label(model.spellCooldownText) {
                addClass(TalentTooltipStyles.tooltipSubtitle)
                prefWidth = PREF_WIDTH / 2.0
                alignment = Pos.CENTER_RIGHT
                visibleAndManagedWhen(model.hasSpellCooldown)
            }
        }
    }

    private fun EventTarget.generateDescription() {
        // current rank description
        label(model.talentDescription) {
            addClass(TalentTooltipStyles.tooltipDescription)
            isWrapText = true
            prefWidth = PREF_WIDTH
        }
        // next rank description
        //spacer
        label {
            visibleAndManagedWhen(model.hasNextRank)
        }
        label(model.nextRankTitle) {
            addClass(TalentTooltipStyles.tooltipSubtitle)
            visibleAndManagedWhen(model.hasNextRank)
        }
        label(model.nextRankDescription) {
            addClass(TalentTooltipStyles.tooltipDescription)
            isWrapText = true
            prefWidth = PREF_WIDTH
            visibleAndManagedWhen(model.hasNextRank)
        }
    }

    private fun EventTarget.generateFooter() {
        // spacer
        label {
            visibleAndManagedWhen(
                model.requiresSpec
                    .or(model.requiresPrerequisite)
                    .or(model.canLearnTalent)
                    .or(model.canUnlearnTalent))
        }

        label(model.requiresSpecText) {
            addClass(TalentTooltipStyles.tooltipError)
            visibleAndManagedWhen(model.requiresSpec)
        }
        label(model.requiresPrerequisiteText) {
            addClass(TalentTooltipStyles.tooltipError)
            visibleAndManagedWhen(model.requiresPrerequisite)
        }
        label(model.learnTalentText) {
            addClass(TalentTooltipStyles.tooltipConfirmation)
            visibleAndManagedWhen(model.canLearnTalent)
        }
        label(model.unlearnTalentText) {
            addClass(TalentTooltipStyles.tooltipError)
            visibleAndManagedWhen(model.canUnlearnTalent)
        }
    }

    private fun Node.visibleAndManagedWhen(observable: ObservableValue<Boolean>) {
        visibleWhen(observable)
        managedWhen(observable)
    }
}