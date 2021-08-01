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
        val surrogate = with(value) { SpecializationSurrogate(translationKey, icon, backgroundImage, talents) }
        encoder.encodeSerializableValue(SpecializationSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Specialization {
        val surrogate = decoder.decodeSerializableValue(SpecializationSurrogate.serializer())
        return with(surrogate) { Specialization(key, icon, backgroundImage, talents.toObservable()) }
    }
}

@Serializable
@SerialName("Specialization")
private class SpecializationSurrogate(
    val key: String,
    val icon: String,
    val backgroundImage: String,
    val talents: List<Talent>
)