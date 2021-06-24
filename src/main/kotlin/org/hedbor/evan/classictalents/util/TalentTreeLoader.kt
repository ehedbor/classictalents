package org.hedbor.evan.classictalents.util

import com.beust.klaxon.Klaxon
import org.hedbor.evan.classictalents.talents.ResourceType
import org.hedbor.evan.classictalents.talents.SpellInfo
import org.hedbor.evan.classictalents.talents.Talent
import org.hedbor.evan.classictalents.talents.TalentTree
import java.io.IOException


private object Dummy

/**
 * Loads a talent tree from the specified path in the resources folder.
 */
fun loadTalentTree(path: String): TalentTree {
    val resource = Dummy::class.java.getResourceAsStream(path) ?: throw IOException("Resource at \"$path\" not found.")
    val wowClass = Klaxon().parse<JsonWowClass>(resource) ?: throw IllegalStateException("Could not parse resource.")
    val spec = wowClass.spec1

    val talentTreeKey = "${wowClass.key}.spec1"
    val backgroundImage = spec.backgroundImage
    val tree = TalentTree(talentTreeKey, wowClass.key, backgroundImage)
    
    for ((talentName, jsonTalent) in spec.talents) {
        val key = "$talentTreeKey.$talentName"
        val icon = jsonTalent.icon
        if (jsonTalent.location.size != 2) malformedProperty("$key.location", "[#, #]")
        val location = jsonTalent.location[0] to jsonTalent.location[1]
        val maxRank = jsonTalent.maxRank
        val prerequisite = if (jsonTalent.prerequisite != null) {
            if (jsonTalent.prerequisite.size != 2) malformedProperty("$key.prerequisite", "[#, #]")
            jsonTalent.prerequisite[0] to jsonTalent.prerequisite[1]
        } else {
            null
        }
        val spellInfo = parseSpell(key, jsonTalent.spell)

        tree.talents += Talent(tree, key, icon, location, maxRank, spellInfo, prerequisite)
    }

    return tree
}

private fun parseSpell(talentKey: String, jsonSpellInfo: JsonSpellInfo?): SpellInfo? {
    if (jsonSpellInfo == null) return null
    
    val castTimeSec = if (jsonSpellInfo.castTime == "instant") 0 else jsonSpellInfo.castTime.toIntOrNull()
        ?: malformedProperty("$talentKey.spell.castTime", "'(instant|#)'")

    var cooldownSec: Int? = null
    if (jsonSpellInfo.cooldown != null) {
        val substrings = jsonSpellInfo.cooldown.split(" ")
        if (substrings.size != 2) malformedProperty("$talentKey.spell.cooldown", "'#.# (sec|min|hr)'")
        val cooldown = substrings[0].toDoubleOrNull() ?: malformedProperty("$talentKey.spell.cooldown", "'#.# (sec|min|hr)'")
        cooldownSec = when (substrings[1]) {
            "sec" -> cooldown.toInt()
            "min" -> (60 * cooldown).toInt()
            "hr" -> (60 * 60 * cooldown).toInt()
            else ->  malformedProperty("$talentKey.spell.cooldown", "'#.# (sec|min|hr)'")
        }
    }

    var range: Int? = null
    if (jsonSpellInfo.range != null) {
        range = if (jsonSpellInfo.range == "melee") {
            0
        } else {
            val substrings = jsonSpellInfo.range.split(" ")
            if (substrings.size != 2) malformedProperty("$talentKey.spell.range", "'melee' or '# yd'")
            substrings[0].toIntOrNull() ?: malformedProperty("$talentKey.spell.range", "'melee' or '# yd'")
        }
    }

    var resourceCost: Int? = null
    var resourceType: ResourceType? = null
    if (jsonSpellInfo.resourceCost != null) {
        val substrings = jsonSpellInfo.resourceCost.split(" ")
        if (substrings.size != 2) malformedProperty("$talentKey.spell.resourceCost", "'# (mana|%mana|rage|energy)'")
        resourceCost = substrings[0].toIntOrNull() ?:  malformedProperty("$talentKey.spell.resourceCost", "'# (mana|%mana|rage|energy)'")
        resourceType = when (substrings[1]) {
            "mana" -> ResourceType.MANA
            "%mana" -> ResourceType.PERCENT_OF_BASE_MANA
            "rage" -> ResourceType.RAGE
            "energy" -> ResourceType.ENERGY
            else -> malformedProperty("$talentKey.spell.resourceCost", "'# (mana|%mana|rage|energy)'")
        }
    }

    return SpellInfo(castTimeSec, cooldownSec, resourceCost, resourceType, range)
}

private fun malformedProperty(key: String, expectedFormat: String): Nothing {
    throw IllegalStateException("JSON property $key is malformed; expected $expectedFormat")
}