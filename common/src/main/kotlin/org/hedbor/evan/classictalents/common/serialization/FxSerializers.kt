package org.hedbor.evan.classictalents.common.serialization

import javafx.beans.property.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import tornadofx.toObservable


object SimpleStringPropertySerializer : KSerializer<SimpleStringProperty> {
    override val descriptor = PrimitiveSerialDescriptor("SimpleStringProperty", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: SimpleStringProperty) = encoder.encodeString(value.value)
    override fun deserialize(decoder: Decoder) = SimpleStringProperty(decoder.decodeString())
}

object SimpleBooleanPropertySerializer : KSerializer<SimpleBooleanProperty> {
    override val descriptor = PrimitiveSerialDescriptor("SimpleBooleanProperty", PrimitiveKind.BOOLEAN)
    override fun serialize(encoder: Encoder, value: SimpleBooleanProperty) = encoder.encodeBoolean(value.value)
    override fun deserialize(decoder: Decoder) = SimpleBooleanProperty(decoder.decodeBoolean())
}

object SimpleIntegerPropertySerializer : KSerializer<SimpleIntegerProperty> {
    override val descriptor = PrimitiveSerialDescriptor("SimpleIntegerProperty", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: SimpleIntegerProperty) = encoder.encodeInt(value.value)
    override fun deserialize(decoder: Decoder) = SimpleIntegerProperty(decoder.decodeInt())
}

object SimpleDoublePropertySerializer : KSerializer<SimpleDoubleProperty> {
    override val descriptor = PrimitiveSerialDescriptor("SimpleDoubleProperty", PrimitiveKind.DOUBLE)
    override fun serialize(encoder: Encoder, value: SimpleDoubleProperty) = encoder.encodeDouble(value.value)
    override fun deserialize(decoder: Decoder) = SimpleDoubleProperty(decoder.decodeDouble())
}

class SimpleObjectPropertySerializer<T>(private val valueSerializer: KSerializer<T>) : KSerializer<SimpleObjectProperty<T>> {
    override val descriptor = buildClassSerialDescriptor("SimpleObjectProperty", valueSerializer.descriptor)
    override fun serialize(encoder: Encoder, value: SimpleObjectProperty<T>) = encoder.encodeSerializableValue(valueSerializer, value.value)
    override fun deserialize(decoder: Decoder) = SimpleObjectProperty(decoder.decodeSerializableValue(valueSerializer))
}

class SimpleListPropertySerializer<E>(elementSerializer: KSerializer<E>) : KSerializer<SimpleListProperty<E>> {
    private val serializer = ListSerializer(elementSerializer)

    override val descriptor = serializer.descriptor
    override fun serialize(encoder: Encoder, value: SimpleListProperty<E>) = serializer.serialize(encoder, value)
    override fun deserialize(decoder: Decoder) = SimpleListProperty(serializer.deserialize(decoder).toObservable())
}

class SimpleMapPropertySerializer<K, V>(keySerializer: KSerializer<K>, valueSerializer: KSerializer<V>) : KSerializer<SimpleMapProperty<K, V>> {
    private val serializer = MapSerializer(keySerializer, valueSerializer)

    override val descriptor = serializer.descriptor
    override fun serialize(encoder: Encoder, value: SimpleMapProperty<K, V>) = serializer.serialize(encoder, value)
    override fun deserialize(decoder: Decoder) = SimpleMapProperty(serializer.deserialize(decoder).toObservable())
}