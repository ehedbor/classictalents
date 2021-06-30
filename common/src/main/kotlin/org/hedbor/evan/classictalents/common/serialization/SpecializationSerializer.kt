package org.hedbor.evan.classictalents.common.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.hedbor.evan.classictalents.common.model.Specialization
import org.hedbor.evan.classictalents.common.model.Talent
import tornadofx.*


internal object SpecializationSerializer : KSerializer<Specialization> {
    override val descriptor = SpecializationSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Specialization) {
        val talents = value.talents.sortedWith(compareBy<Talent> { it.location.column }.thenBy { it.location.row })
        val surrogate = with(value) { SpecializationSurrogate(translationKey, backgroundImage, talents) }
        encoder.encodeSerializableValue(SpecializationSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Specialization {
        val surrogate = decoder.decodeSerializableValue(SpecializationSurrogate.serializer())
        return with(surrogate) { Specialization("", key, backgroundImage, talents.toObservable()) }
    }
}

/**
 * [Specialization.displayName] is not stored in the JSON representation of this object,
 * as it is stored in a `.properties` file instead.
 *
 * @see Specialization
 */
@Serializable
@SerialName("Specialization")
private class SpecializationSurrogate(
    val key: String,
    val backgroundImage: String,
    val talents: List<Talent>
)