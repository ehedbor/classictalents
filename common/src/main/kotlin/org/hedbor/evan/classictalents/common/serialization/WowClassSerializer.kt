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
import org.hedbor.evan.classictalents.common.model.Era
import org.hedbor.evan.classictalents.common.model.Specialization
import org.hedbor.evan.classictalents.common.model.WowClass
import tornadofx.toObservable


internal object WowClassSerializer : KSerializer<WowClass> {
    override val descriptor = WowClassSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: WowClass) {
        val specs = value.specializations.sortedBy { it.translationKey }
        val surrogate = WowClassSurrogate(value.translationKey, value.era, specs)
        encoder.encodeSerializableValue(WowClassSurrogate.serializer(), surrogate)

    }

    override fun deserialize(decoder: Decoder): WowClass {
        val surrogate = decoder.decodeSerializableValue(WowClassSurrogate.serializer())
        return with(surrogate) { WowClass(key, era, specs.toObservable()) }
    }
}

@Serializable
@SerialName("Class")
private class WowClassSurrogate(
    val key: String,
    val era: Era,
    val specs: List<Specialization>
)