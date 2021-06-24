package org.hedbor.evan.talenttreegenerator.model.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.hedbor.evan.talenttreegenerator.model.Specialization
import org.hedbor.evan.talenttreegenerator.model.Talent
import org.hedbor.evan.talenttreegenerator.model.WowClass
import tornadofx.mapEach
import tornadofx.toObservable


object SpecializationSerializer : KSerializer<Specialization> {
    override val descriptor = SpecializationSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Specialization) {
        val talents = value.talents
            .filter { it != null && !it.displayName.isNullOrEmpty() }
            .associateBy { it.translationKey }

        val surrogate = SpecializationSurrogate(value.backgroundImage, talents)
        encoder.encodeSerializableValue(SpecializationSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Specialization {
        val surrogate = decoder.decodeSerializableValue(SpecializationSurrogate.serializer())
        val talents = surrogate.talents.mapEach { this.value.also { it.translationKey = this.key } }.toObservable()
        return Specialization(backgroundImage = surrogate.backgroundImage, talents = talents)
    }
}

/**
 * [Specialization.displayName] and [Specialization.translationKey] are not stored
 * in the JSON representation of this object. [Specialization.translationKey] is the
 * name of this object in its parent ([WowClass]), and [Specialization.displayName]
 * is stored in a resource bundle.
 *
 * @see Specialization
 */
@Serializable
@SerialName("Specialization")
private class SpecializationSurrogate(
    val backgroundImage: String,
    val talents: Map<String, Talent>
)