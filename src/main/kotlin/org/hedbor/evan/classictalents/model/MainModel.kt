package org.hedbor.evan.classictalents.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.dto.TalentConfigReader
import org.hedbor.evan.classictalents.util.delegate
import org.hedbor.evan.classictalents.util.observableListOf

@Suppress("unused")
class MainModel {
    private val _classes = SimpleListProperty<WowClass>(observableListOf())
    var classes: ObservableList<WowClass> by _classes.delegate()
    fun classesProperty() = _classes

    private val _selectedClass = SimpleObjectProperty<WowClass?>()
    var selectedClass by _selectedClass.delegate()
    fun selectedClassProperty() = _selectedClass

    fun loadClasses() {
        check(classes.isEmpty()) { "Cannot load classes more than once" }

        val classFileNames = listOf(
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
        classFileNames
            .map { reader.readClass(it) }
            .sortedBy { it.name }
            .toCollection(classes)

    }
}
