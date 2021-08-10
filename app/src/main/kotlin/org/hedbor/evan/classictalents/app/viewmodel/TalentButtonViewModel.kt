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

package org.hedbor.evan.classictalents.app.viewmodel

import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.hedbor.evan.classictalents.app.model.Specialization
import org.hedbor.evan.classictalents.app.model.Talent
import org.hedbor.evan.classictalents.app.model.WowClass
import org.hedbor.evan.classictalents.app.service.ImageService
import org.hedbor.evan.classictalents.app.view.TalentTooltip
import tornadofx.ViewModel
import tornadofx.objectBinding
import tornadofx.or
import tornadofx.stringBinding


class TalentButtonViewModel(wowClass: WowClass, specialization: Specialization, talent: Talent) : ViewModel() {
    companion object {
        const val BORDER_SIZE = 68.0
        const val HILITE_SIZE = 62.0
        const val INNER_ICON_SIZE = 60.0
    }

    private val props = TalentProperties(wowClass, specialization, talent)

    val borderImage = ImageService.loadImage("/images/Icon/large/border/default.png", BORDER_SIZE, BORDER_SIZE)
    val borderHiliteImage = ImageService.loadImage("/images/Icon/large/hilite/hilite.png", HILITE_SIZE, HILITE_SIZE)
    val backgroundImage = objectBinding(props.isAllocatable, props.hasBeenAllocated) {
        if (props.isAllocatable.value || props.hasBeenAllocated.value) {
            props.normalBackgroundImage
        } else {
            props.grayscaleBackgroundImage
        }
    }

    val useActiveBorder = props.canAcceptPoints
    val useMaxedOutBorder = props.isMaxedOut

    val buttonWidth = borderImage.width
    val buttonHeight = borderImage.height

    val rankCounterText = talent.rankProperty.stringBinding { it.toString() }
    val useRankCounterText = props.isAllocatable or props.hasBeenAllocated

    val tooltip = TalentTooltip(TalentTooltipViewModel(props))

    fun onMouseClicked(event: MouseEvent) {
        when (event.button) {
            MouseButton.PRIMARY -> {
                if (props.isAllocatable.value && !props.isMaxedOut.value) {
                    props.talent.rank += 1
                }
            }
            MouseButton.SECONDARY -> {
                if (props.isDeallocatable.value && props.hasBeenAllocated.value) {
                    props.talent.rank -= 1
                }
            }
            else -> {}
        }
    }
}