package org.hedbor.evan.classictalents.model

import org.hedbor.evan.classictalents.util.*


@Suppress("MemberVisibilityCanBePrivate")
class Spell {
    var cost by property<Int>()
    fun costProperty() = getProperty(Spell::cost)

    var resource by property<ResourceType>()
    fun resourceProperty() = getProperty(Spell::resource)

    var range by property<Double>()
    fun rangeProperty() = getProperty(Spell::range)

    var minRange by property<Double>()
    fun minRangeProperty() = getProperty(Spell::minRange)

    var castTime by property<Double>()
    fun castTimeProperty() = getProperty(Spell::castTime)

    var isChanneled by property<Boolean>()
    fun channeledProperty() = getProperty(Spell::isChanneled)

    var cooldown by property<Double>()
    fun cooldownProperty() = getProperty(Spell::cooldown)

    var cooldownUnit by property<CooldownUnit>()
    fun cooldownUnitProperty() = getProperty(Spell::cooldownUnit)

    var reagents by property(observableListOf<String>())
    fun reagentsProperty() = getProperty<String>(Spell::reagents)

    var tools by property(observableListOf<String>())
    fun toolsProperty() = getProperty<String>(Spell::tools)
}

enum class ResourceType {
    MANA,
    PERCENT_OF_BASE_MANA,
    RAGE,
    ENERGY,
}

enum class CooldownUnit {
    SECONDS,
    MINUTES,
    HOURS
}