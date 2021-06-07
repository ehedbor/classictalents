package org.hedbor.evan.talenttreegenerator.model

import javafx.beans.property.SimpleIntegerProperty
import tornadofx.getValue
import tornadofx.setValue


class Location(row: Int = Integer.MIN_VALUE, column: Int = Int.MIN_VALUE) {
    val rowProperty = SimpleIntegerProperty(this, "row", row)
    var row by rowProperty

    val columnProperty = SimpleIntegerProperty(this, "column", column)
    var column by columnProperty
}