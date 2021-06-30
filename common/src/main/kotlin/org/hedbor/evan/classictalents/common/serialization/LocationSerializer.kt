package org.hedbor.evan.classictalents.common.serialization

import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.*
import org.hedbor.evan.classictalents.common.model.Location

internal object LocationSerializer : KSerializer<Location> {
    override val descriptor: SerialDescriptor = LocationDescriptor

    override fun serialize(encoder: Encoder, value: Location) {
        encoder.encodeStructure(IntArraySerializer().descriptor) {
            encodeIntElement(Int.serializer().descriptor, 0, value.row)
            encodeIntElement(Int.serializer().descriptor, 1, value.column)
        }
    }

    override fun deserialize(decoder: Decoder): Location {
        // kotlin requires that these have default values because the compiler
        // cannot guarantee that the block provided to decodeStructure is ever executed
        var row: Int = -1
        var col: Int = -1
        decoder.decodeStructure(IntArraySerializer().descriptor) {
            val size = decodeCollectionSize(Int.serializer().descriptor)
            val unknownSize = (size == -1)
            if (!unknownSize && size != 2) { throw SerializationException("Expected int array of size 2. Actual size: $size") }
            while (true) {
                when (val index = decodeElementIndex(Int.serializer().descriptor)) {
                    0 -> row = decodeIntElement(Int.serializer().descriptor, 0)
                    1 -> col = decodeIntElement(Int.serializer().descriptor, 1)
                    DECODE_DONE -> break
                    else -> throw SerializationException("Unexpected index $index. Expected int array of size 2.")
                }
            }
        }
        return Location(row, col)
    }
}

@OptIn(ExperimentalSerializationApi::class)
private object LocationDescriptor : SerialDescriptor {
    override val serialName = "Location"
    override val kind = StructureKind.LIST
    override val elementsCount = 1

    override fun getElementName(index: Int): String = index.toString()

    override fun getElementIndex(name: String): Int =
        name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid index")

    override fun isElementOptional(index: Int): Boolean {
        require(index in 0..1) { "Index out of range: $serialName only has 2 elements [row, col]" }
        return false
    }

    override fun getElementAnnotations(index: Int): List<Annotation> {
        require(index in 0..1) { "Index out of range: $serialName only has 2 elements [row, col]" }
        return emptyList()
    }

    override fun getElementDescriptor(index: Int): SerialDescriptor {
        require(index in 0..1) { "Index out of range: $serialName only has 2 elements [row, col]" }
        return Int.serializer().descriptor
    }
}
