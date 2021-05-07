package org.hedbor.evan.classictalents.talents

import tornadofx.*


class Talent(val key: String, val icon: String, val location: Pair<Int, Int>, val maxRank: Int, val spellInfo: SpellInfo? = null, val prerequisite: Pair<Int, Int>? = null) {
    var allocatedPoints: Int by property(0)
    fun allocatedPointsProperty() = getProperty(Talent::allocatedPoints)
}

class SpellInfo(
    val castTimeSec: Int,
    val cooldownSec: Int? = null,
    val resourceCost: Int? = null,
    val resourceType: ResourceType? = null,
    // a range of '0' is considered to be melee range
    val rangeYds: Int? = null
)

enum class ResourceType {
    MANA,
    PERCENT_OF_BASE_MANA,
    RAGE,
    ENERGY
}