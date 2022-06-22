package org.hedbor.evan.classictalents.model

import javafx.beans.property.SimpleMapProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.dto.TalentConfigReader
import org.hedbor.evan.classictalents.util.*

@Suppress("unused")
class MainModel {
    private val _classes = SimpleMapProperty<Expansion, ObservableList<WowClass>>(emptyObservableMap())
    var classes: ObservableMap<Expansion, ObservableList<WowClass>> by _classes.delegate()
    fun classesProperty() = _classes

    private val _selectedExpansion = SimpleObjectProperty<Expansion?>()
    var selectedExpansion by _selectedExpansion.delegate()
    fun selectedExpansionProperty() = _selectedExpansion

    private val _selectedClass = SimpleObjectProperty<WowClass?>()
    var selectedClass by _selectedClass.delegate()
    fun selectedClassProperty() = _selectedClass

    fun loadClasses() {
        check(classes.isEmpty()) { "Cannot load classes more than once" }

        val classFileNames = sequenceOf(
            "$ASSETS_ROOT/talents/Druid.yml",
            "$ASSETS_ROOT/talents/Hunter.yml",
            "$ASSETS_ROOT/talents/Mage.yml",
            "$ASSETS_ROOT/talents/Paladin.yml",
            "$ASSETS_ROOT/talents/Priest.yml",
            "$ASSETS_ROOT/talents/Rogue.yml",
            "$ASSETS_ROOT/talents/Shaman.yml",
            "$ASSETS_ROOT/talents/Warlock.yml",
            "$ASSETS_ROOT/talents/Warrior.yml",
        )
        val reader = TalentConfigReader()
        classes = classFileNames
            .map { reader.readClass(it) }
            .filter { it.isNotEmpty() }
            .flatten()
            .groupBy { it.expansion }
            .mapValues { (_, classes) -> classes.sortedBy { it.name }.toObservableList() }
            .toObservableMap()

    }
}
