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

package org.hedbor.evan.classictalents.common.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.hedbor.evan.classictalents.common.model.CooldownUnit
import org.hedbor.evan.classictalents.common.model.Range
import org.hedbor.evan.classictalents.common.model.ResourceType
import org.hedbor.evan.classictalents.common.model.Spell


internal object SpellSerializer : KSerializer<Spell> {
    override val descriptor = SpellSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Spell) {
        var resourceCost: String? = null
        if (value.resourceType != null && value.resourceCost > 0) {
            resourceCost = "${value.resourceCost} ${value.resourceType!!.serialName}"
        }

        val range = when {
            value.range < 0.0 -> null
            Range.isSelf(value.range) -> null
            Range.isMelee(value.range) -> "melee"
            else -> "${value.range} yd"
        }

        var castTime = "instant"
        if (value.castTime > 0.0001) {
            castTime = "${value.castTime} sec"
        }

        var cooldown: String? = null
        if (value.cooldownUnit != null && value.cooldown > 0.0) {
            cooldown = "${value.cooldown} ${value.cooldownUnit!!.serialName}"
        }

        val surrogate = SpellSurrogate(resourceCost, range, castTime, cooldown)
        encoder.encodeSerializableValue(SpellSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Spell {
        val surrogate = decoder.decodeSerializableValue(SpellSurrogate.serializer())
        val result = Spell()
        if (surrogate.resourceCost != null) {
            val substrings = surrogate.resourceCost.split(" ")
            if (substrings.size != 2) {
                malformedProperty("resourceCost")
            }

            result.resourceCost = substrings[0].toIntOrNull() ?: malformedProperty("resourceCost")
            result.resourceType = when (substrings[1]) {
                ResourceType.MANA.serialName -> ResourceType.MANA
                ResourceType.PERCENT_OF_BASE_MANA.serialName -> ResourceType.PERCENT_OF_BASE_MANA
                ResourceType.ENERGY.serialName -> ResourceType.ENERGY
                ResourceType.RAGE.serialName -> ResourceType.RAGE
                else -> malformedProperty("resourceCost")
            }
        }

        result.range = when (surrogate.range) {
            null -> Range.SELF
            "melee" -> Range.MELEE
            else -> {
                val substrings = surrogate.range.split(" ")
                if (substrings.size != 2 || substrings[1] != "yd") {
                    malformedProperty("range")
                }
                substrings[0].toDoubleOrNull() ?: malformedProperty("range")
            }
        }

        result.castTime = if (surrogate.castTime == "instant") {
            0.0
        } else {
            val substrings = surrogate.castTime.split(" ")
            if (substrings.size != 2 || substrings[1] != "sec") {
                malformedProperty("castTime")
            }
            substrings[0].toDoubleOrNull() ?: malformedProperty("castTime")
        }

        if (surrogate.cooldown != null) {
            val substrings = surrogate.cooldown.split(" ")
            if (substrings.size != 2) {
                malformedProperty("cooldown")
            }
            result.cooldown = substrings[0].toDoubleOrNull() ?: malformedProperty("cooldown")
            result.cooldownUnit = when (substrings[1]) {
                CooldownUnit.HOURS.serialName -> CooldownUnit.HOURS
                CooldownUnit.MINUTES.serialName -> CooldownUnit.MINUTES
                CooldownUnit.SECONDS.serialName -> CooldownUnit.SECONDS
                else -> malformedProperty("cooldown")
            }
        }

        return result
    }

    private fun malformedProperty(key: String): Nothing {
        val format = when (key) {
            "resourceCost" -> "# (mana|percent_of_base_mana|rage|energy)"
            "range" -> "(melee|#.# yd)"
            "castTime" -> "(instant|#.# sec)"
            "cooldown" ->  "#.# (sec|min|hr)"
            else -> throw IllegalArgumentException("Unknown format for key '$key'")
        }
        throw SerializationException("Spell property '$key' is malformed; expected '$format'")
    }
}

@Serializable
@SerialName("Spell")
private class SpellSurrogate(
    val resourceCost: String? = null,
    val range: String? = null,
    val castTime: String,
    val cooldown: String? = null
)
