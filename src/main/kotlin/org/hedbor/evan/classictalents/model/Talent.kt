package org.hedbor.evan.classictalents.model

import org.hedbor.evan.classictalents.util.getProperty
import org.hedbor.evan.classictalents.util.property


@Suppress("MemberVisibilityCanBePrivate")
class Talent {
    var name by property<String>()
    fun nameProperty() = getProperty(Talent::name)

    var location by property<Pair<Int, Int>>()
    fun locationProperty() = getProperty(Talent::location)

    var prerequisite by property<String>()
    fun prerequisiteProperty() = getProperty(Talent::prerequisite)

    var maxRank by property<Int>()
    fun maxRankProperty() = getProperty(Talent::maxRank)

    var rank by property<Int>()
    fun rankProperty() = getProperty(Talent::rank)

    var icon by property<String>()
    fun iconProperty() = getProperty(Talent::icon)

    var description by property<String>()
    fun descriptionProperty() = getProperty(Talent::description)

    var spell by property<Spell>()
    fun spellProperty() = getProperty(Talent::spell)
}