package org.hedbor.evan.talenttreegenerator.model.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import org.hedbor.evan.talenttreegenerator.model.Location


object LocationSerializer : KSerializer<Location> {
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
            row = decodeIntElement(Int.serializer().descriptor, 0)
            col = decodeIntElement(Int.serializer().descriptor, 1)
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
