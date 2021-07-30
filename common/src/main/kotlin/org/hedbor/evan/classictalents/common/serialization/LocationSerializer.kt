/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2021 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
