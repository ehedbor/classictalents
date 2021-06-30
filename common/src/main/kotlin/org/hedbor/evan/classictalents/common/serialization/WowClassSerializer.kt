package org.hedbor.evan.classictalents.common.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.hedbor.evan.classictalents.common.model.Specialization
import org.hedbor.evan.classictalents.common.model.WowClass
import tornadofx.*


internal object WowClassSerializer : KSerializer<WowClass> {
    override val descriptor = WowClassSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: WowClass) {
        val specs = value.specializations.sortedBy { it.displayName }
        val surrogate = WowClassSurrogate(value.translationKey, specs)
        encoder.encodeSerializableValue(WowClassSurrogate.serializer(), surrogate)

    }

    override fun deserialize(decoder: Decoder): WowClass {
        val surrogate = decoder.decodeSerializableValue(WowClassSurrogate.serializer())
        return WowClass(translationKey = surrogate.key, specializations = surrogate.specs.toObservable())
    }
}

/**
 * [WowClass.displayName] is not stored in the JSON representation of this object,
 * as it is stored in a `.properties` file instead.
 *
 * @see Specialization
 */
@Serializable
@SerialName("Class")
private class WowClassSurrogate(
    val key: String,
    val specs: List<Specialization>
)