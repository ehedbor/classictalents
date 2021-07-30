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


/** @see Specialization.TALENT_COLUMN_COUNT */
@Serializable
enum class Era(val translationKey: String, val talentRowCount: Int, val maxLevel: Int) {
    @SerialName("classic")  CLASSIC("era.classic", 7, 60),
    @SerialName("tbc")      TBC("era.tbc", 9, 70),
    @SerialName("wotlk")    WOTLK("era.wotlk", 11, 80);

    override fun toString() = translationKey

    fun getAvailablePoints(level: Int): Int {
        require(level in 1..maxLevel)
        if (level < 10) return 0
        return level - 9
    }
}