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
import org.hedbor.evan.classictalents.common.model.Location
import org.hedbor.evan.classictalents.common.model.Spell
import org.hedbor.evan.classictalents.common.model.Talent


internal object TalentSerializer : KSerializer<Talent> {
    override val descriptor = TalentSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Talent) {
        val surrogate = with(value) {
            TalentSurrogate(translationKey, location, prerequisite, maxRank, icon, spell)
        }
        encoder.encodeSerializableValue(TalentSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Talent {
        val surrogate = decoder.decodeSerializableValue(TalentSurrogate.serializer())
        return with(surrogate) {
            Talent(key, location, prerequisite, maxRank, icon, spell)
        }
    }
}

@Serializable
@SerialName("Talent")
private class TalentSurrogate(
    val key: String,
    val location: Location,
    @SerialName("requires")
    val prerequisite: Location? = null,
    val maxRank: Int,
    val icon: String,
    val spell: Spell? = null,
)
