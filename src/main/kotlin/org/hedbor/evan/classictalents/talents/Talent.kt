package org.hedbor.evan.classictalents.talents

import tornadofx.booleanBinding
import tornadofx.getProperty
import tornadofx.property


class Talent(val tree: TalentTree, val key: String, val icon: String, val location: Pair<Int, Int>, val maxRank: Int, val spellInfo: SpellInfo? = null, val prerequisiteLocation: Pair<Int, Int>? = null) {
    var allocatedPoints: Int by property(0)
    fun allocatedPointsProperty() = getProperty(Talent::allocatedPoints)

    val talentRowUnlocked = tree.totalAllocatedPoints.booleanBinding { it as Int >= requiredPoints }

    val shouldBeActive = talentRowUnlocked.booleanBinding { talentRowUnlocked ->
        val prereq = prerequisite
        val prereqAtMaxRank = prereq?.allocatedPoints == prereq?.maxRank
        talentRowUnlocked!! && prereqAtMaxRank
    }

    val prerequisite: Talent? by lazy {
        if (prerequisiteLocation != null)
            tree.talents.first { it.location == prerequisiteLocation }
        else null
    }

    val requiredPoints = 5 * location.first
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