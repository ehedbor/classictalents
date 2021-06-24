package org.hedbor.evan.talenttreegenerator.model.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.hedbor.evan.talenttreegenerator.model.Spell


object SpellSerializer : KSerializer<Spell> {
    override val descriptor = SpellSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Spell) {
        var resourceCost: String? = null
        if (value.hasResource) {
            resourceCost = value.resourceCost.toString() + " " +
                if (value.resourceType == "% of base mana") "%mana" else value.resourceType
        }
        var range: String? = null
        if (value.hasRange) {
            range = if (value.range == 0) {
                "melee"
            } else {
                "${value.range} yd"
            }
        }

        var castTime = "instant"
        if (value.isNotInstantCast) {
            castTime = "${value.castTime} sec"
        }

        var cooldown: String? = null
        if (value.hasCooldown) {
            cooldown = "${value.cooldown} ${value.cooldownUnit}"
        }

        val surrogate = SpellSurrogate(resourceCost, range, castTime, cooldown)
        encoder.encodeSerializableValue(SpellSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Spell {
        val surrogate = decoder.decodeSerializableValue(SpellSurrogate.serializer())

        return Spell().apply {
            if (surrogate.resourceCost != null) {
                val substrings = surrogate.resourceCost.split(" ")
                if (substrings.size != 2) {
                    malformedProperty("resourceCost", "# (mana|%mana|rage|energy)")
                }

                hasResource = true
                resourceCost = substrings[0].toIntOrNull()
                    ?: malformedProperty("resourceCost", "# (mana|%mana|rage|energy)")
                resourceType = when (val resourceType = substrings[1]) {
                    "%mana" -> "% of base mana"
                    "mana", "rage", "energy" -> resourceType
                    else -> malformedProperty("resourceCost", "# (mana|%mana|rage|energy)")
                }
            }

            if (surrogate.range != null) {
                hasRange = true
                range = if (surrogate.range == "melee") {
                    0
                } else {
                    val substrings = surrogate.range.split(" ")
                    if (substrings.size != 2) {
                        malformedProperty("range", "(melee|# yd)")
                    }
                    substrings[0].toIntOrNull() ?: malformedProperty("range", "(melee|# yd)")
                }
            }

            isNotInstantCast = (surrogate.castTime != "instant")
            if (isNotInstantCast) {
                castTime = surrogate.castTime.toDoubleOrNull() ?: malformedProperty("castTime", "(instant|#.#)")
            }

            if (surrogate.cooldown != null) {
                val substrings = surrogate.cooldown.split(" ")
                if (substrings.size != 2) {
                    malformedProperty("cooldown", "#.# (sec|min|hr)")
                }
                cooldown = substrings[0].toDoubleOrNull() ?: malformedProperty("cooldown", "#.# (sec|min|hr)'")
                cooldownUnit = when (val unit = substrings[1]) {
                    "sec", "min", "hr" -> unit
                    else -> malformedProperty("cooldown", "#.# (sec|min|hr)")
                }
            }
        }
    }

    private fun malformedProperty(key: String, format: String): Nothing {
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
