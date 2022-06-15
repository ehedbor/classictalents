package org.hedbor.evan.classictalents.dto

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import javafx.scene.image.Image
import javafx.scene.paint.Color
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.model.*
import org.hedbor.evan.classictalents.util.observableListOf
import org.hedbor.evan.classictalents.util.toObservableList

class TalentConfigReader {
    companion object {
        private val COST_PATTERN = Regex("""(?<cost>\d+)\s*(?<resource>%\s*of\s*base\s*mana|mana|rage|energy)""", RegexOption.IGNORE_CASE)
        private val RANGE_PATTERN = Regex("""(?<keyword>self|melee)|(?<distance>\d+(?:\.\d+)?)\s*(?<unit>yards?|yds?)""", RegexOption.IGNORE_CASE)
        private val CAST_TIME_PATTERN = Regex("""(?<keyword>instant)|(?<time>\d+(?:\.\d+)?)\s*(?<unit>seconds?|secs?)""", RegexOption.IGNORE_CASE)
        private val COOLDOWN_PATTERN = Regex("""(?<cooldown>\d+(?:\.\d+)?)\s*(?<unit>seconds?|secs?|minutes?|mins?|hours?|hrs?)""", RegexOption.IGNORE_CASE)
    }
    
    private val mapper = ObjectMapper(YAMLFactory())
    private val rootType = object : TypeReference<TalentConfigDto>() {}

    /**
     * Reads a talent file from the specified resource location.
     *
     * @param filePath a path to a resource file
     * @return the parsed [WowClass]
     * @throws IllegalStateException if an unexpected error is found while parsing
     * @throws IllegalArgumentException if the yml file is malformed
     */
    fun readClass(filePath: String): WowClass {
        val config = try {
            mapper.readValue(javaClass.getResourceAsStream(filePath), rootType)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to parse talent file at '$filePath'", e)
        }

        // construct a wow class from the config file
        val wowClass = WowClass()

        require(config.size == 1) {
            "Expected 1 class per file, found ${config.size}\n\tin file '$filePath'"
        }
        val (className, classDto) = config.entries.first()

        val errorPath = "in file '$filePath', class '$className'"

        wowClass.name = className
        wowClass.icon = getImage(errorPath, classDto.icon)
        wowClass.color = try {
            Color.web(classDto.color)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Could not parse color '${classDto.color}'\n\t$errorPath", e)
        }

        for ((specName, specDto) in classDto.classic.specs) {
            wowClass.specializations += readSpec(
                "$errorPath, spec '$specName'",
                specName,
                specDto
            )
        }

        return wowClass
    }

    private fun readSpec(errorPath: String, specName: String, specDto: SpecDto): Specialization {
        val spec = Specialization()
        spec.name = specName
        spec.icon = getImage(errorPath, specDto.icon)
        spec.background = getImage(errorPath, specDto.background)

        for ((talentName, talentDto) in specDto.talents) {
            require(specDto.talents.count { it.value.location == talentDto.location } == 1) {
                "Found multiple talents with the same location (${talentDto.location})\n\t$errorPath"
            }
            val talentErrorPath = "$errorPath, talent '$talentName'"
            spec.talents += readTalent(talentErrorPath, talentName, talentDto)
        }

        // now that all talents have been loaded, resolve prerequisites
        for ((_, talentDto) in specDto.talents) {
            if (talentDto.requires == null) continue
            val talent = spec.talents.firstOrNull { it.row == talentDto.location[0] && it.column == talentDto.location[1] }
            val prereq = spec.talents.firstOrNull { it.name == talentDto.requires }

            require(talent != null) {
                "Couldn't find talent at (${talentDto.location[0]}, ${talentDto.location[1]})." +
                    " This should not happen.\n\t$errorPath"
            }
            require(prereq != null) {
                "Couldn't find prerequisite talent called '${talentDto.requires}'\n\t$errorPath, talent '${talent.name}'"
            }
            talent.prerequisite = prereq
        }

        return spec
    }

    private fun readTalent(errorPath: String, talentName: String, talentDto: TalentDto): Talent {
        val talent = Talent()
        talent.name = talentName

        require(talentDto.location.size == 2) {
            "Location must be an array of the form [row, col] (got ${talentDto.location})\n\t$errorPath"
        }
        // TODO: different number of rows depending on expansion
        require(talentDto.location[0] in 0 until 7) {
            "Location row '${talentDto.location[0]}' out of range 0..6\n\t$errorPath"
        }
        require(talentDto.location[1] in 0 until 4) {
            "Location column '${talentDto.location[1]}' out of range 0..3\n\t$errorPath"
        }
        talent.row = talentDto.location[0]
        talent.column = talentDto.location[1]

        // resolve prerequisites later; all talents must be loaded first.

        if (talentDto.spell != null) {
            require(talentDto.maxRank == 1) {
                "Max rank for spells must be 1 (got ${talentDto.maxRank})\n\t$errorPath"
            }
        }
        talent.maxRank = talentDto.maxRank

        talent.icon = getImage(errorPath, talentDto.icon)

        // TODO: validate choice format
        talent.description = talentDto.description

        if (talentDto.spell != null) {
            talent.spell = readSpell(errorPath, talentDto.spell)
        }

        return talent
    }

    private fun readSpell(errorPath: String, spellDto: SpellDto): Spell {
        val spell = Spell()

        spell.tools = spellDto.tools?.toObservableList() ?: observableListOf()
        spell.reagents = spellDto.reagents?.toObservableList() ?: observableListOf()

        if (spellDto.cost != null) {
            val match = COST_PATTERN.find(spellDto.cost)
            requireNotNull(match) {
                "Spell cost must be of the form [<cost:int> <resource:Mana|Rage|Energy|% of Base Mana>] " +
                    "(got '${spellDto.cost}')\n\t$errorPath"
            }

            spell.cost = match.groups["cost"]?.value?.toIntOrNull()
                ?: throw IllegalStateException("failed to parse resource cost despite matching regex")

            val resource = match.groups["resource"]?.value
                ?.lowercase()
                ?.replace("\\s+".toRegex(), " ")
            spell.resource = when (resource) {
                "mana" -> ResourceType.MANA
                "% of base mana" -> ResourceType.PERCENT_OF_BASE_MANA
                "energy" -> ResourceType.ENERGY
                "rage" -> ResourceType.RAGE
                else -> throw IllegalStateException("failed to parse resource despite matching regex")
            }
        }

        if (spellDto.range != null) {
            val match = RANGE_PATTERN.find(spellDto.range)
            requireNotNull(match) {
                "Spell range must be of the form [<keyword:Self|Melee>|<distance:float> <unit:yd>] " +
                    "(got '${spellDto.range}')\n\t$errorPath"
            }

            spell.range = if (match.groups["keyword"] != null) {
                val keyword = match.groups["keyword"]?.value?.lowercase()
                when (keyword) {
                    "self" -> 0.0
                    "melee" -> 5.0
                    else -> throw IllegalStateException("failed to parse range keyword despite matching regex")
                }
            } else {
                val distance = match.groups["distance"]?.value?.toDoubleOrNull()
                    ?: throw IllegalArgumentException("failed to parse range distance despite matching regex")

                // no need to check for unit type as it is always yards
                requireNotNull(match.groups["unit"]) { "failed to parse range unit despite matching regex" }

                distance
            }
        }

        run {
            val match = CAST_TIME_PATTERN.find(spellDto.castTime)
            requireNotNull(match) {
                "Spell cast time must be of the form [<keyword:Instant>|<time:float> <unit:sec>] " +
                    "(got '${spellDto.castTime}')\n\t$errorPath"
            }

            spell.castTime = if (match.groups["keyword"] != null) {
                // no need to check keyword as it is always "instant"
                0.0
            } else {
                val time = match.groups["time"]?.value?.toDoubleOrNull()
                    ?: throw IllegalArgumentException("failed to parse cast time despite matching regex")

                // no need to check for unit type as it is always seconds
                requireNotNull(match.groups["unit"]) { "failed to parse cast time unit despite matching regex" }
                time
            }
        }

        if (spellDto.cooldown != null) {
            val match = COOLDOWN_PATTERN.find(spellDto.cooldown)
            requireNotNull(match) {
                "Spell cooldown must be of the form [<cooldown:float> <unit:sec|min|hr>] " +
                    "(got '${spellDto.range}')\n\t$errorPath"
            }

            spell.cooldown = match.groups["cooldown"]?.value?.toDoubleOrNull()
                ?: throw IllegalStateException("failed to parse cooldown time despite matching regex")

            val unit = match.groups["unit"]?.value?.lowercase()
            spell.cooldownUnit = when (unit) {
                "sec", "secs", "second", "seconds" -> CooldownUnit.SECONDS
                "min", "mins", "minute", "minutes" -> CooldownUnit.MINUTES
                "hr", "hrs", "hour", "hours" -> CooldownUnit.HOURS
                else -> throw IllegalStateException("failed to parse cooldown unit despite matching regex")
            }
        }

        return spell
    }

    private fun getImage(errorPath: String, path: String): Image {
        val actualPath = if (path.startsWith("/")) {
            path
        } else {
            "$ASSETS_ROOT/$path"
        }

        val resource = javaClass.getResourceAsStream(actualPath)
        require(resource != null) {
            "Could not find image at '$path' (absolute='$actualPath')\n\t$errorPath"
        }
        return Image(resource)
    }
}