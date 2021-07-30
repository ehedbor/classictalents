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

package org.hedbor.evan.classictalents.common.model

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.Serializable
import org.hedbor.evan.classictalents.common.serialization.SpellSerializer
import tornadofx.getValue
import tornadofx.setValue


@Suppress("MemberVisibilityCanBePrivate")
@Serializable(with = SpellSerializer::class)
class Spell(
    resourceCost: Int = 0,
    resourceType: ResourceType? = null,
    castTime: Double = 0.0,
    cooldown: Double = 0.0,
    cooldownUnit: CooldownUnit? = null,
    range: Double = 0.0
) {
    val resourceCostProperty = SimpleIntegerProperty(this, "resourceCost", resourceCost)
    var resourceCost: Int by resourceCostProperty

    val resourceTypeProperty = SimpleObjectProperty<ResourceType>(this, "resourceType", resourceType)
    var resourceType: ResourceType? by resourceTypeProperty

    val castTimeProperty = SimpleDoubleProperty(this, "castTime", castTime)
    var castTime: Double by castTimeProperty

    val cooldownProperty = SimpleDoubleProperty(this, "cooldown", cooldown)
    var cooldown: Double by cooldownProperty

    val cooldownUnitProperty = SimpleObjectProperty<CooldownUnit>(this, "cooldownUnit", cooldownUnit)
    var cooldownUnit: CooldownUnit? by cooldownUnitProperty

    val rangeProperty = SimpleDoubleProperty(this, "range", range)
    var range: Double by rangeProperty

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Spell) return false

        if (resourceCost != other.resourceCost) return false
        if (resourceType != other.resourceType) return false
        if (castTime != other.castTime) return false
        if (cooldown != other.cooldown) return false
        if (cooldownUnit != other.cooldownUnit) return false
        if (range != other.range) return false

        return true
    }

    override fun hashCode(): Int {
        var result = resourceCost
        result = 31 * result + (resourceType?.hashCode() ?: 0)
        result = 31 * result + castTime.hashCode()
        result = 31 * result + cooldown.hashCode()
        result = 31 * result + (cooldownUnit?.hashCode() ?: 0)
        result = 31 * result + range.hashCode()
        return result
    }

    override fun toString(): String {
        return "Spell(resourceCost=$resourceCost, resourceType=$resourceType, castTime=$castTime, cooldown=$cooldown, cooldownUnit=$cooldownUnit, range=$range)"
    }
}

enum class ResourceType(val translationKey: String, val serialName: String) {
    MANA("unit.resource.mana", "mana"),
    PERCENT_OF_BASE_MANA("unit.resource.percent_of_base_mana", "%mana"),
    RAGE("unit.resource.rage", "rage"),
    ENERGY("unit.resource.energy", "energy");

    override fun toString() = translationKey
}

enum class CooldownUnit(val translationKey: String, val serialName: String) {
    HOURS("unit.time.hours", "hr"),
    MINUTES("unit.time.minutes", "min"),
    SECONDS("unit.time.seconds", "sec");

    override fun toString() = translationKey
}

object Range {
    const val SELF = 0.0
    const val MELEE = 5.0

    fun isSelf(range: Double): Boolean {
        require(range >= 0.0) { "Range must be positive or zero." }
        return range == 0.0
    }

    fun isMelee(range: Double): Boolean {
        require(range >= 0.0) { "Range must be positive or zero." }
        return range > 0.0 && range <= 5.0
    }
}