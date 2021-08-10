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

package org.hedbor.evan.classictalents.app.model

import org.hedbor.evan.classictalents.common.model.LocationData
import org.hedbor.evan.classictalents.common.serialization.ModelFor


class Location(row: Int = 0, column: Int = 0) : ModelFor<LocationData> {
    var row = row
        private set
    var column = column
        private set

    override fun toData(): LocationData {
        return LocationData(row, column)
    }

    override fun fromData(data: LocationData): Location {
        row = data.row
        column = data.column
        return this
    }

    override fun equals(other: Any?): Boolean {
        return (other as? Location)?.let { row == it.row && column == it.column } ?: false
    }

    override fun hashCode(): Int {
        return row + column * 31
    }

    override fun toString(): String {
        return "[$row, $column]"
    }
}