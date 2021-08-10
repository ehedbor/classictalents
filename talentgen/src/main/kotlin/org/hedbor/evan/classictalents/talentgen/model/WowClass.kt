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

package org.hedbor.evan.classictalents.talentgen.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import org.hedbor.evan.classictalents.common.model.Era
import org.hedbor.evan.classictalents.common.model.WowClassData
import org.hedbor.evan.classictalents.common.serialization.ModelFor
import tornadofx.asObservable
import tornadofx.getValue
import tornadofx.observableListOf
import tornadofx.setValue


@Suppress("MemberVisibilityCanBePrivate")
class WowClass(
    translationKey: String = "",
    icon: String = "",
    era: Era = Era.CLASSIC,
    specializations: ObservableList<Specialization> = observableListOf()
) : ModelFor<WowClassData> {
    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey: String by translationKeyProperty

    val iconProperty = SimpleStringProperty(this, "icon", icon)
    var icon by iconProperty

    val eraProperty = SimpleObjectProperty<Era>(this, "era", era)
    var era: Era by eraProperty

    val specializationsProperty: SimpleListProperty<Specialization> = SimpleListProperty(this, "specializations", specializations)
    var specializations: ObservableList<Specialization> by specializationsProperty

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
        specializations = data.specializations
            .mapTo(ArrayList(data.specializations.size)) { Specialization().fromData(it) }
            .asObservable()

        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WowClass) return false

        if (translationKey != other.translationKey) return false
        if (icon != other.icon) return false
        if (era != other.era) return false
        if (specializations != other.specializations) return false

        return true
    }

    override fun hashCode(): Int {
        var result = translationKey.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + era.hashCode()
        result = 31 * result + specializations.hashCode()
        return result
    }

    override fun toString(): String {
        return "WowClass(translationKey='$translationKey', icon='$icon', era=$era, specializations=$specializations)"
    }
}

