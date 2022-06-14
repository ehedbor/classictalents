package org.hedbor.evan.classictalents.model

import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.dto.TalentConfigReader
import org.hedbor.evan.classictalents.util.getProperty
import org.hedbor.evan.classictalents.util.observableListOf
import org.hedbor.evan.classictalents.util.property

class MainModel {
    var classes by property(observableListOf<WowClass>())
    fun classesProperty() = getProperty(MainModel::classes)

    var selectedClass by property<WowClass?>()
    fun selectedClassProperty() = getProperty(MainModel::selectedClass)

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
