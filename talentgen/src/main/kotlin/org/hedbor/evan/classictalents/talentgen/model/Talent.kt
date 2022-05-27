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

package org.hedbor.evan.classictalents.talentgen.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.hedbor.evan.classictalents.common.model.TalentData
import org.hedbor.evan.classictalents.common.serialization.ModelFor
import tornadofx.getValue
import tornadofx.setValue


@Suppress("MemberVisibilityCanBePrivate")
class Talent(
    translationKey: String = "",
    location: Location = Location(),
    prerequisite: Location? = null,
    maxRank: Int = 0,
    icon: String = "",
    spell: Spell? = null
) : ModelFor<TalentData> {
    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey: String by translationKeyProperty

    val locationProperty = SimpleObjectProperty(this, "location", location)
    var location: Location by locationProperty

    val prerequisiteProperty = SimpleObjectProperty<Location>(this, "prerequisite", prerequisite)
    var prerequisite: Location? by prerequisiteProperty

    val maxRankProperty = SimpleIntegerProperty(this, "maxRank", maxRank)
    var maxRank: Int by maxRankProperty

    val iconProperty = SimpleStringProperty(this, "icon", icon)
    var icon: String by iconProperty

    val spellProperty = SimpleObjectProperty<Spell>(this, "spell", spell)
    var spell: Spell? by spellProperty

    override fun toData(): TalentData {
        return TalentData(translationKey, location.toData(), prerequisite?.toData(), maxRank, icon, spell?.toData())
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Talent) return false

        if (translationKey != other.translationKey) return false
        if (location != other.location) return false
        if (prerequisite != other.prerequisite) return false
        if (maxRank != other.maxRank) return false
        if (icon != other.icon) return false
        if (spell != other.spell) return false

        return true
    }

    override fun hashCode(): Int {
        var result = translationKey.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + prerequisite.hashCode()
        result = 31 * result + maxRank
        result = 31 * result + icon.hashCode()
        result = 31 * result + spell.hashCode()
        return result
    }

    override fun toString(): String {
        return "Talent(translationKey='$translationKey', location=$location, prerequisite=$prerequisite, maxRank=$maxRank, icon='$icon', spell=$spell)"
    }
}