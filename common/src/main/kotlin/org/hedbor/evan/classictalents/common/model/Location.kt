package org.hedbor.evan.classictalents.common.model

import javafx.beans.property.SimpleIntegerProperty
import kotlinx.serialization.Serializable
import org.hedbor.evan.classictalents.common.serialization.LocationSerializer
import tornadofx.*


@Suppress("MemberVisibilityCanBePrivate")
@Serializable(with = LocationSerializer::class)
class Location(row: Int = 0, column: Int = 0) {
    val rowProperty = SimpleIntegerProperty(this, "row", row)
    var row by rowProperty

    val columnProperty = SimpleIntegerProperty(this, "column", column)
    var column by columnProperty

    override fun equals(other: Any?): Boolean {
        return (other as Location?)?.let {
            it.row == row && it.column == column
        } ?: false
    }

    override fun hashCode() = 31 * row + column

    override fun toString()= "[$row, $column]"
}