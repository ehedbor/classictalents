package org.hedbor.evan.classictalents.model

import javafx.beans.property.ObjectProperty
import javafx.scene.image.Image
import org.hedbor.evan.classictalents.util.getProperty
import org.hedbor.evan.classictalents.util.property


@Suppress("MemberVisibilityCanBePrivate")
class Talent {
    var name by property<String>()
    fun nameProperty() = getProperty(Talent::name)

    var row by property<Int>()
    fun rowProperty() = getProperty(Talent::row)

    var column by property<Int>()
    fun columnProperty() = getProperty(Talent::column)

    var prerequisite by property<Talent?>()
    fun prerequisiteProperty() = getProperty(Talent::prerequisite)

    var maxRank by property<Int>()
    fun maxRankProperty() = getProperty(Talent::maxRank)

    var rank by property<Int>()
    fun rankProperty() = getProperty(Talent::rank)

    var icon by property<Image>()
    fun iconProperty() = getProperty(Talent::icon)

    var description by property<String>()
    fun descriptionProperty() = getProperty(Talent::description)

    var spell by property<Spell?>()
    fun spellProperty() = getProperty(Talent::spell) as ObjectProperty<Spell?>
}