package org.hedbor.evan.talenttreegenerator.model.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.hedbor.evan.talenttreegenerator.model.Location
import org.hedbor.evan.talenttreegenerator.model.Specialization
import org.hedbor.evan.talenttreegenerator.model.Spell
import org.hedbor.evan.talenttreegenerator.model.Talent


object TalentSerializer : KSerializer<Talent> {
    override val descriptor = TalentSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Talent) {
        val prerequisite = if (value.hasPrerequisite) value.prerequisite else null
        val spell = if (value.isSpell) value.spell else null
        val surrogate = TalentSurrogate(value.location, value.maxRank, prerequisite, spell, value.icon)

        encoder.encodeSerializableValue(TalentSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Talent {
        val surrogate = decoder.decodeSerializableValue(TalentSurrogate.serializer())
        return Talent(
            location = surrogate.location,
            maxRank = surrogate.maxRank,
            icon = surrogate.icon
        ).apply {
            if (surrogate.prerequisite != null) {
                hasPrerequisite = true
                prerequisite = surrogate.prerequisite
            }
            if (surrogate.spell != null) {
                isSpell = true
                spell = surrogate.spell
            }
        }
    }
}

/**
 * [Talent.displayName], [Talent.translationKey] and [Talent.description] are not stored
 * in the JSON representation of this object. displayName and description are stored in
 * a resource bundle, and translationKey is stored in its parent ([Specialization]).
 *
 * @see Talent
 */
@Serializable
@SerialName("Talent")
private class TalentSurrogate(
    val location: Location,
    val maxRank: Int,
    @SerialName("requires") val prerequisite: Location? = null,
    val spell: Spell? = null,
    val icon: String,
)
