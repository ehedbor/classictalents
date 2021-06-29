package org.hedbor.evan.classictalents.app.model

import javafx.beans.binding.Bindings
import tornadofx.booleanBinding
import tornadofx.getProperty
import tornadofx.property


class Talent(val tree: TalentTree, val key: String, val icon: String, val location: Pair<Int, Int>, val maxRank: Int, val spellInfo: SpellInfo? = null, val prerequisiteLocation: Pair<Int, Int>? = null) {
    var allocatedPoints: Int by property(0)
    fun allocatedPointsProperty() = getProperty(Talent::allocatedPoints)

    val talentRowUnlocked = tree.totalAllocatedPoints.booleanBinding { it as Int >= requiredPoints }

    val canRemovePoints = Bindings.createBooleanBinding({
            if (allocatedPoints <= 0) return@createBooleanBinding false

            // you cannot remove points if doing so would cause a later talent's prerequisite to not be met
            // first, check the talent that requires this talent, if any
            val dependency = tree.talents.firstOrNull { this.location == it.prerequisiteLocation }
            if (dependency != null && dependency.allocatedPoints > 0) {
                return@createBooleanBinding false
            }

            // next, find the row of the highest allocated talent.
            // if this talent is in the final row, a point can be removed
            val highestTalent = tree.talents.filter { it.allocatedPoints > 0 }.maxByOrNull { it.location.first }!!
            if (location.first == highestTalent.location.first) {
                return@createBooleanBinding true
            }

            // otherwise, determine the total number of allocated points in all tiers lower than the highest.
            // if removing a point would cause this total to be less than the requirement of the highest talent, a point cannot be removed
            val totalPoints = tree.talents.filter { it.location.first < highestTalent.location.first }.sumOf { it.allocatedPoints }
            totalPoints - 1 >= highestTalent.requiredPoints
        }, tree.talents)

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