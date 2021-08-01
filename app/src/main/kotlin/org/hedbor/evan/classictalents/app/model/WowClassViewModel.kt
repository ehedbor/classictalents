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

package org.hedbor.evan.classictalents.app.model

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import org.hedbor.evan.classictalents.common.model.WowClass
import tornadofx.*


class WowClassViewModel(initialWowClass: WowClass) : ViewModel() {
    private val wowClassProperty = SimpleObjectProperty(initialWowClass)
    private var wowClass: WowClass by wowClassProperty

    val translationKey = bind { wowClass.translationKeyProperty }

    val icon = bind { wowClass.iconProperty }.objectBinding { it?.runCatching { Image(it) }?.getOrNull() }
    val borderImage = Image("/images/Icon/large/border/default.png")
    val borderHiliteImage = Image("/images/Icon/large/hilite/hilite.png")

    val era = bind { wowClass.eraProperty }

    val specializations = bind { wowClass.specializationsProperty }

    private val totalAllocatedSpecPoints = run {
        // note that this will NOT WORK if elements are added or removed!
        // this shouldn't be a problem because this application will not change the models (aside from the talent's rank)
        val specTalents = specializations.map {
                spec -> spec.talents.map { talent -> talent.rankProperty }
        }
        val bindableTalents = specTalents.flatten().toTypedArray()
        Bindings.createObjectBinding({
            specTalents.map { spec -> spec.sumOf { it.value } }
        }, *bindableTalents)!!
    }

    val totalAllocatedPoints = totalAllocatedSpecPoints.integerBinding { it!!.sum() }

    val talentInfoText = translationKey.stringBinding(totalAllocatedSpecPoints) {
        val className = messages[translationKey.value]
        val specInfo = totalAllocatedSpecPoints.value.joinToString(separator = "/")
        messages.format("class.talent_info", className, specInfo)
    }

    val requiredLevelText = totalAllocatedPoints.stringBinding(era) {
        val format = if (totalAllocatedPoints.value > 0) {
            era.value.getRequiredLevel(totalAllocatedPoints.value)
        } else {
            "--"
        }
        messages.format("class.required_level", format)
    }

    val remainingPointsText = totalAllocatedPoints.stringBinding(era) {
        val pointsAtMax = era.value.getAvailablePoints(era.value.maxLevel)
        messages.format("class.remaining_points", pointsAtMax - totalAllocatedPoints.value)

    }

    init {
        rebindOnChange(wowClassProperty)
    }
}