package org.hedbor.evan.classictalents.common.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.hedbor.evan.classictalents.common.model.Location

internal object LocationSerializer : KSerializer<Location> {
    private val serializer = IntArraySerializer()

    override val descriptor = serializer.descriptor

    override fun serialize(encoder: Encoder, value: Location) {
        val arr = intArrayOf(value.row, value.column)
        encoder.encodeSerializableValue(serializer, arr)
    }

    override fun deserialize(decoder: Decoder): Location {
        val arr = decoder.decodeSerializableValue(serializer)
        if (arr.size != 2) {
            throw SerializationException("Expected int array of size 2. Actual size: ${arr.size}")
        }
        return Location(arr[0], arr[1])
    }
}
