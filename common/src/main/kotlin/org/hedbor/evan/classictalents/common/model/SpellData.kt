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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("Spell")
data class SpellData(
    val resourceCost: Int = 0,
    val resourceType: ResourceType? = null,
    val range: Range = Range(0.0),
    val castTime: Double = 0.0,
    val cooldown: Double = 0.0,
    val cooldownUnit: CooldownUnit? = null,
)

@Serializable
enum class ResourceType(val translationKey: String) {
    @SerialName("mana") MANA("unit.resource.mana"),
    @SerialName("%mana") PERCENT_OF_BASE_MANA("unit.resource.percent_of_base_mana"),
    @SerialName("rage") RAGE("unit.resource.rage"),
    @SerialName("energy") ENERGY("unit.resource.energy");

    override fun toString() = translationKey
}

@Serializable
enum class CooldownUnit(val translationKey: String) {
    @SerialName("sec") SECONDS("unit.time.seconds"),
    @SerialName("min") MINUTES("unit.time.minutes"),
    @SerialName("hr") HOURS("unit.time.hours");

    override fun toString() = translationKey
}

@Serializable
@JvmInline
value class Range(val distanceYds: Double) {
    companion object {
        val SELF = Range(0.0)
        val MELEE = Range(5.0)
    }

    init {
        require(distanceYds >= 0.0) { "Range must be positive or zero." }
    }

    val isSelf get() = (distanceYds == 0.0)
    val isMelee get()  = (!isSelf && distanceYds <= 5.0)

    override fun toString() = distanceYds.toString()
}