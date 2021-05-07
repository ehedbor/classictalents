package org.hedbor.evan.classictalents.talents

import tornadofx.*


class Talent(val key: String, val icon: String, val location: Pair<Int, Int>, val maxRank: Int, val ability: Ability? = null, val prerequisite: Pair<Int, Int>? = null) {
    init {
        require(maxRank in 1..5) { "Talents may only have a maximum rank ranging from 1 to 5." }
    }

    var allocatedPoints: Int by property(0)
    fun allocatedPointsProperty() = getProperty(Talent::allocatedPoints)
}

class Ability(
    castTimeSec: Double,
    cooldown: Double? = null,
    cooldownUnit: TimeUnit? = null,
    resourceCost: Int? = null,
    resourceType: ResourceType? = null,
    range: Int? = null,
    rangeType: RangeType? = null)

enum class TimeUnit {
    SECONDS,
    MINUTES,
    HOURS
}

enum class ResourceType {
    MANA,
    PERCENT_OF_BASE_MANA,
    RAGE,
    ENERGY
}

enum class RangeType {
    MELEE,
    YARDS
}