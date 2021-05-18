package org.hedbor.evan.classictalents.talents

import javafx.beans.binding.Bindings
import javafx.beans.binding.IntegerBinding
import javafx.collections.ObservableList
import tornadofx.observableListOf


class TalentTree(val key: String, val wowClassKey: String, val backgroundImage: String) {
    val talents: ObservableList<Talent> = observableListOf { arrayOf(it.allocatedPointsProperty()) }

    var totalAllocatedPoints: IntegerBinding =
        Bindings.createIntegerBinding({ this.talents.sumOf { it.allocatedPoints } }, this.talents)
}