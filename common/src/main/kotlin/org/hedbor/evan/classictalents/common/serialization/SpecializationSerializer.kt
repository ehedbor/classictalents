package org.hedbor.evan.classictalents.common.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.hedbor.evan.classictalents.common.model.Specialization
import org.hedbor.evan.classictalents.common.model.Talent
import tornadofx.toObservable


internal class SpecializationSerializer : KSerializer<Specialization> {
    override val descriptor = SpecializationSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Specialization) {
        val talents = value.talents
            .filter { it.translationKey.isNotBlank() }
            .sortedWith(compareBy<Talent> { it.location.row }.thenBy { it.location.column })
        val surrogate = with(value) { SpecializationSurrogate(translationKey, backgroundImage, talents) }
        encoder.encodeSerializableValue(SpecializationSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Specialization {
        val surrogate = decoder.decodeSerializableValue(SpecializationSurrogate.serializer())
        return with(surrogate) { Specialization(key, backgroundImage, talents.toObservable()) }
    }
}

@Serializable
@SerialName("Specialization")
private class SpecializationSurrogate(
    val key: String,
    val backgroundImage: String,
    val talents: List<Talent>
)