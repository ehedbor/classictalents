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

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import org.hedbor.evan.classictalents.common.model.SpecializationData
import org.hedbor.evan.classictalents.common.serialization.ModelFor
import tornadofx.asObservable
import tornadofx.getValue
import tornadofx.observableListOf
import tornadofx.setValue


class Specialization(
    translationKey: String = "",
    icon: String = "",
    backgroundImage: String = "",
    talents: ObservableList<Talent> = observableListOf()
) : ModelFor<SpecializationData> {
    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey: String by translationKeyProperty

    val iconProperty = SimpleStringProperty(this, "icon", icon)
    var icon: String by iconProperty

    val backgroundImageProperty = SimpleStringProperty(this, "backgroundImage", backgroundImage)
    var backgroundImage: String by backgroundImageProperty

    val talentsProperty = SimpleListProperty(this, "talents", talents)
    var talents: ObservableList<Talent> by talentsProperty

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
        talents = data.talents
            .mapTo(ArrayList(data.talents.size)) { Talent().fromData(it) }
            .asObservable()

        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Specialization) return false

        if (translationKey != other.translationKey) return false
        if (icon != other.icon) return false
        if (backgroundImage != other.backgroundImage) return false
        if (talents != other.talents) return false

        return true
    }

    override fun hashCode(): Int {
        var result = translationKey.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + backgroundImage.hashCode()
        result = 31 * result + talents.hashCode()
        return result
    }

    override fun toString(): String {
        return "Specialization(translationKey='$translationKey', icon'$icon', backgroundImage='$backgroundImage', talents=$talents)"
    }
}
