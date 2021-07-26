package org.hedbor.evan.classictalents.common.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.hedbor.evan.classictalents.common.model.Location
import org.hedbor.evan.classictalents.common.model.Spell
import org.hedbor.evan.classictalents.common.model.Talent


internal object TalentSerializer : KSerializer<Talent> {
    override val descriptor = TalentSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Talent) {
        val surrogate = with(value) {
            TalentSurrogate(translationKey, location, prerequisite, maxRank, icon, spell)
        }
        encoder.encodeSerializableValue(TalentSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Talent {
        val surrogate = decoder.decodeSerializableValue(TalentSurrogate.serializer())
        return with(surrogate) {
            Talent(key, location, prerequisite, maxRank, icon, spell)
        }
    }
}

@Serializable
@SerialName("Talent")
private class TalentSurrogate(
    val key: String,
    val location: Location,
    @SerialName("requires")
    val prerequisite: Location? = null,
    val maxRank: Int,
    val icon: String,
    val spell: Spell? = null,
)
