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

package org.hedbor.evan.classictalents.app.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import org.hedbor.evan.classictalents.common.model.Era
import org.hedbor.evan.classictalents.common.model.WowClassData
import org.hedbor.evan.classictalents.common.serialization.ModelFor
import tornadofx.booleanBinding
import tornadofx.getValue
import tornadofx.integerBinding
import tornadofx.setValue


class WowClass(
    translationKey: String = "",
    icon: String = "",
    era: Era = Era.CLASSIC,
    specs: List<Specialization> = listOf()
) : ModelFor<WowClassData> {
    var translationKey = translationKey
        private set
    var icon = icon
        private set
    var era = era
        private set

    val specializationsProperty = SimpleListProperty(
        FXCollections.observableList(specs.toMutableList()) { spec ->
            arrayOf(spec.talentsProperty)
        })
    var specializations by specializationsProperty

    val totalPointsProperty = SimpleIntegerProperty().apply {
        bind(specializationsProperty.integerBinding { allSpecs ->
            allSpecs!!.sumOf { it.totalPoints }
        })}
    val totalPoints by totalPointsProperty

    val hasUnassignedPointsProperty = totalPointsProperty.booleanBinding {
        val pointsAtMax = era.getAvailablePoints(era.maxLevel)
        totalPoints < pointsAtMax
    }
    val hasUnassignedPoints by hasUnassignedPointsProperty

    init {
        totalPointsProperty
    }

    override fun toData(): WowClassData {
        val specsData = specializations
            .filter { it.translationKey.isNotBlank() }
            .sortedBy { it.translationKey }
            .map { it.toData() }

        return WowClassData(
            translationKey,
            icon,
            era,
            specsData
        )
    }

    override fun fromData(data: WowClassData): WowClass {
        translationKey = data.translationKey
        icon = data.icon
        era = data.era

        val newSpecs = data.specializations.map { Specialization().fromData(it) }
        specializations.setAll(newSpecs)

        return this
    }
}