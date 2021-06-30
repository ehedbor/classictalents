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
import java.lang.IllegalArgumentException


internal object SpellSerializer : KSerializer<Spell> {
    override val descriptor = SpellSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Spell) {
        var resourceCost: String? = null
        if (value.resourceType != null && value.resourceCost > 0) {
            resourceCost = "${value.resourceCost} ${value.resourceType}"
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
            cooldown = "${value.cooldown} ${value.cooldownUnit}"
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
                "${ResourceType.MANA}" -> ResourceType.MANA
                "${ResourceType.PERCENT_OF_BASE_MANA}" -> ResourceType.PERCENT_OF_BASE_MANA
                "${ResourceType.ENERGY}" -> ResourceType.ENERGY
                "${ResourceType.RAGE}" -> ResourceType.RAGE
                else -> malformedProperty("resourceCost")
            }
        }

        result.range = when (surrogate.range) {
            null -> Range.SELF
            "melee" -> Range.MELEE
            else -> {
                val substrings = surrogate.range.split(" ")
                if (substrings.size != 2) {
                    malformedProperty("range")
                }
                substrings[0].toDoubleOrNull() ?: malformedProperty("range")
            }
        }

        result.castTime = if (surrogate.castTime == "instant") {
            0.0
        } else {
            surrogate.castTime.toDoubleOrNull() ?: malformedProperty("castTime")
        }

        if (surrogate.cooldown != null) {
            val substrings = surrogate.cooldown.split(" ")
            if (substrings.size != 2) {
                malformedProperty("cooldown")
            }
            result.cooldown = substrings[0].toDoubleOrNull() ?: malformedProperty("cooldown")
            result.cooldownUnit = when (substrings[1]) {
                "${CooldownUnit.HOURS}" -> CooldownUnit.HOURS
                "${CooldownUnit.MINUTES}" -> CooldownUnit.MINUTES
                "${CooldownUnit.SECONDS}" -> CooldownUnit.SECONDS
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
