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
import org.hedbor.evan.classictalents.common.model.TalentData
import org.hedbor.evan.classictalents.common.serialization.ModelFor
import tornadofx.getValue
import tornadofx.setValue


class Talent(
     translationKey: String = "",
     location: Location = Location(),
     prerequisite: Location? = null,
     maxRank: Int = 0,
     rank: Int = 0,
     icon: String = "",
     spell: Spell? = null
) : ModelFor<TalentData> {
    var translationKey = translationKey
        private set
    var location = location
        private set
    var prerequisite = prerequisite
        private set
    var maxRank = maxRank
        private set
    var icon = icon
        private set
    var spell: Spell? = spell
        private set

    val rankProperty = SimpleIntegerProperty(rank)
    var rank by rankProperty

    override fun toData(): TalentData {
        return TalentData(
            translationKey,
            location.toData(),
            prerequisite?.toData(),
            maxRank,
            icon,
            spell?.toData())
    }

    override fun fromData(data: TalentData): Talent {
        translationKey = data.translationKey
        location.fromData(data.location)
        prerequisite = data.prerequisite?.let { Location().fromData(it) }
        maxRank = data.maxRank
        icon = data.icon
        spell = data.spell?.let { Spell().fromData(it) }

        return this
    }
}