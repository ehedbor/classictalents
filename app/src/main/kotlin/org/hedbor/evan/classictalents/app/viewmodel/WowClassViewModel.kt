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

import org.hedbor.evan.classictalents.app.model.WowClass
import org.hedbor.evan.classictalents.app.service.ImageService
import tornadofx.ViewModel
import tornadofx.format
import tornadofx.get
import tornadofx.stringBinding


class WowClassViewModel(val wowClass: WowClass) : ViewModel() {
    val fullKey = wowClass.translationKey
    val specializations = bind { wowClass.specializationsProperty }

    val icon = ImageService.loadImage(wowClass.icon)
    val borderImage = ImageService.loadImage("/images/Icon/large/border/default.png")
    val borderHiliteImage = ImageService.loadImage("/images/Icon/large/hilite/hilite.png")

    val talentInfoText = wowClass.specializationsProperty.stringBinding { specs ->
        val className = messages[fullKey]
        val specInfo = specs!!.map { it.totalPoints }.joinToString(separator = "/")
        messages.format("class.talent_info", className, specInfo)
    }

    val requiredLevelText = wowClass.totalPointsProperty.stringBinding { totalPoints ->
        val format = if (totalPoints as Int > 0) {
            wowClass.era.getRequiredLevel(totalPoints)
        } else {
            "--"
        }
        messages.format("class.required_level", format)
    }

    val remainingPointsText = wowClass.totalPointsProperty.stringBinding { totalPoints ->
        val pointsAtMax = wowClass.era.getAvailablePoints(wowClass.era.maxLevel)
        messages.format("class.remaining_points", pointsAtMax - totalPoints as Int)

    }
}