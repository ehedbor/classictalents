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

package org.hedbor.evan.classictalents.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("Talent")
data class TalentData(
    @SerialName("key") val translationKey: String,
    val location: LocationData,
    @SerialName("requires") val prerequisite: LocationData? = null,
    val maxRank: Int,
    val icon: String,
    val spell: SpellData? = null
) {
    companion object {
        const val MINIMUM_RANK = 1
        const val MAXIMUM_PERMISSIBLE_RANK = 5
    }
}