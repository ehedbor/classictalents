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

import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Tooltip
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import org.hedbor.evan.classictalents.app.model.TalentTooltipViewModel
import org.hedbor.evan.classictalents.app.view.styles.TalentTooltipStyles
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
                visibleAndManagedWhen(textProperty().isNotNull)
            }
            label(model.spellRangeText) {
                addClass(TalentTooltipStyles.tooltipSubtitle)
                prefWidth = PREF_WIDTH / 2.0
                alignmentProperty().bind(model.spellRangeAlignment)
                visibleAndManagedWhen(textProperty().isNotNull)
            }
        }
        hbox {
            visibleAndManagedWhen(model.hasSpell)
            label(model.spellCastTimeText) {
                addClass(TalentTooltipStyles.tooltipSubtitle)
                prefWidth = PREF_WIDTH / 2.0
            }
            label(model.spellCooldownText) {
                addClass(TalentTooltipStyles.tooltipSubtitle)
                prefWidth = PREF_WIDTH / 2.0
                alignment = Pos.CENTER_RIGHT
                visibleAndManagedWhen(textProperty().isNotNull)
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
            visibleAndManagedWhen(model.shouldShowNextRankDesc)
        }
        label(model.nextRankTitle) {
            addClass(TalentTooltipStyles.tooltipSubtitle)
            visibleAndManagedWhen(model.shouldShowNextRankDesc)
        }
        label(model.nextRankDescription) {
            addClass(TalentTooltipStyles.tooltipDescription)
            isWrapText = true
            prefWidth = PREF_WIDTH
            visibleAndManagedWhen(model.shouldShowNextRankDesc)
        }
    }

    private fun EventTarget.generateFooter() {
        // spacer
        label {}

        label(model.requiresSpecText) {
            addClass(TalentTooltipStyles.tooltipError)
            visibleAndManagedWhen(model.shouldShowRequiresSpecText)
        }
        label(model.requiresPrerequisiteText) {
            addClass(TalentTooltipStyles.tooltipError)
            visibleAndManagedWhen(model.shouldShowRequiresPrerequisiteText)
        }
        label(model.learnTalentText) {
            addClass(TalentTooltipStyles.tooltipConfirmation)
            visibleAndManagedWhen(model.shouldShowLearnTalentText)
        }
        label(model.unlearnTalentText) {
            addClass(TalentTooltipStyles.tooltipError)
            visibleAndManagedWhen(model.shouldShowUnlearnTalentText)
        }
    }

    private fun Node.visibleAndManagedWhen(observable: ObservableValue<Boolean>) {
        visibleWhen(observable)
        managedWhen(observable)
    }
}