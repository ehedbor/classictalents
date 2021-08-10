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

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import org.hedbor.evan.classictalents.common.model.SpecializationData
import org.hedbor.evan.classictalents.common.serialization.ModelFor
import tornadofx.getValue
import tornadofx.integerBinding
import tornadofx.setValue

class Specialization(
    translationKey: String = "",
    icon: String = "",
    backgroundImage: String = "",
    talents: List<Talent> = listOf()
) : ModelFor<SpecializationData> {
    var translationKey: String = translationKey
        private set
    var icon = icon
        private set
    var backgroundImage = backgroundImage
        private set

    val talentsProperty = SimpleListProperty(FXCollections.observableList(talents.toMutableList()) { arrayOf(it.rankProperty) })
    var talents by talentsProperty

    val totalPointsProperty = SimpleIntegerProperty().apply {
        bind(talentsProperty.integerBinding { allTalents ->
            allTalents!!.sumOf { it.rank }
        })
    }
    val totalPoints by totalPointsProperty

    override fun toData(): SpecializationData {
        val talentsData = talents
            .filter { it.translationKey.isNotBlank() }
            .sortedWith(compareBy<Talent> { it.location.row }.thenBy { it.location.column })
            .map { it.toData() }

        return SpecializationData(
            translationKey,
            icon,
            backgroundImage,
            talentsData
        )
    }

    override fun fromData(data: SpecializationData): Specialization {
        translationKey = data.translationKey
        icon = data.icon
        backgroundImage = data.backgroundImage

        val newTalents = data.talents.map { Talent().fromData(it) }
        talents.setAll(newTalents)

        return this

    }
}