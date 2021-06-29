package org.hedbor.evan.classictalents.talentgen.model.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.hedbor.evan.talenttreegenerator.model.Specialization
import org.hedbor.evan.talenttreegenerator.model.WowClass
import tornadofx.mapEach
import tornadofx.toObservable


object WowClassSerializer : KSerializer<WowClass> {
    override val descriptor = WowClassSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: WowClass) {
        val specs = value.specializations
            .filter { it != null && it.validated }
            .associateBy { it.translationKey }
        val toEncode = linkedMapOf(value.translationKey to WowClassSurrogate(specs))
        val serializer = MapSerializer(String.serializer(), WowClassSurrogate.serializer())
        encoder.encodeSerializableValue(serializer, toEncode)
    }

    override fun deserialize(decoder: Decoder): WowClass {
        val serializer = MapSerializer(String.serializer(), WowClassSurrogate.serializer())
        val decoded = decoder.decodeSerializableValue(serializer)
        if (decoded.size != 1) throw SerializationException("Expected map of size 1.")
        val (key, surrogate) = decoded.entries.first()

        val wowClass = WowClass()
        wowClass.translationKey = key
        wowClass.specializations =
            surrogate.specs.mapEach { this.value.also { it.translationKey = this.key } }.toObservable()
        
        return wowClass
    }
}

@Serializable
@SerialName("Class")
private class WowClassSurrogate(
    val specs: Map<String, Specialization>
)