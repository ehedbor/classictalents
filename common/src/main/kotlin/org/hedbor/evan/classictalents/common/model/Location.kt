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