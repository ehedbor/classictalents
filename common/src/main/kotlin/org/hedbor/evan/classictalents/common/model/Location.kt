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

import javafx.beans.property.SimpleIntegerProperty
import kotlinx.serialization.Serializable
import org.hedbor.evan.classictalents.common.serialization.LocationSerializer
import tornadofx.getValue
import tornadofx.setValue
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Suppress


@Suppress("MemberVisibilityCanBePrivate")
@Serializable(with = LocationSerializer::class)
class Location(row: Int = 0, column: Int = 0)  {
    val rowProperty = SimpleIntegerProperty(this, "row", row)
    var row by rowProperty

    val columnProperty = SimpleIntegerProperty(this, "column", column)
    var column by columnProperty

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Location) return false
        return row == other.row && column == other.column
    }

    override fun hashCode() = 31 * row + column

    override fun toString()= "[$row, $column]"
}