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
        private val COST_PATTERN = Regex("""^\s*(?<cost>\d+)\s*(?<resource>%\s*of\s*base\s*mana|mana|rage|energy|(?:blood|frost|unholy)\s*runes?|runic\s*power)\s*$""", RegexOption.IGNORE_CASE)
        private val RANGE_PATTERN = Regex("""^\s*(?:(?<keyword>self|melee)|(?:(?<minDistance>\d+(?:\.\d+)?)\s*-\s*)?(?<distance>\d+(?:\.\d+)?)\s*(?<unit>yards?|yds?))\s*$""", RegexOption.IGNORE_CASE)
        private val CAST_TIME_PATTERN = Regex("""^\s*(?:(?<keyword>instant|next\s*melee)|(?<time>\d+(?:\.\d+)?)\s*(?<unit>seconds?|secs?)\s*(?<isChanneled>\(channeled\))?)\s*$""", RegexOption.IGNORE_CASE)
        private val COOLDOWN_PATTERN = Regex("""^\s*(?<cooldown>\d+(?:\.\d+)?)\s*(?<unit>seconds?|secs?|minutes?|mins?|hours?|hrs?)\s*$""", RegexOption.IGNORE_CASE)
    }
    
    private val mapper = ObjectMapper(YAMLFactory())

    /**
     * Reads a talent file from the specified resource location.
     *
     * @param filePath a path to a resource file
     * @return the parsed [WowClass]
     * @throws IllegalStateException if an unexpected error is found while parsing
     * @throws IllegalArgumentException if the yml file is malformed
     */
    fun readClass(filePath: String): List<WowClass> {
        val classDto = try {
            mapper.readValue(javaClass.getResourceAsStream(filePath), ClassDto::class.java)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to parse talent file at '$filePath'", e)
        }

        val errorPath = "in file '$filePath', class '${classDto.name}'"

        // these attributes are declared in the root node and shared by all WowClass instances 
        val icon = getImage(errorPath, classDto.icon)
        val color = try {
            Color.web(classDto.color)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Could not parse color '${classDto.color}'\n\t$errorPath", e)
        }

        fun WowClass.addCommonAttributes() {
            this.name = classDto.name
            this.icon = icon
            this.color = color
        }

        val vanillaClass = classDto.classic?.let {
            WowClass().apply {
                expansion = Expansion.CLASSIC
                addCommonAttributes()
                addSpecs("$errorPath, expansion 'Classic'", it.specs)
            }
        }

        val tbcClass = classDto.tbc?.let {
            WowClass().apply {
                expansion = Expansion.TBC
                addCommonAttributes()
                addSpecs("$errorPath, expansion 'TBC'", it.specs)
            }
        }

        val wrathClass = classDto.wotlk?.let {
            WowClass().apply {
                expansion = Expansion.WOTLK
                addCommonAttributes()
                val wrathErrPath = "$errorPath, expansion 'WotLK'"
                addSpecs(wrathErrPath, it.specs)
                //addGlyphs(wrathErrPath, it.glyphs)
            }
        }

        return listOfNotNull(vanillaClass, tbcClass, wrathClass)
    }

    private fun WowClass.addSpecs(errorPath: String, specs: Map<String, SpecDto>) {
        for ((specName, specDto) in specs) {
            val spec = readSpec(
                "$errorPath, spec '$specName'",
                specName,
                specDto
            )
            spec.wowClass = this
            this.specializations += spec
        }
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

            val talent = readTalent(talentErrorPath, talentName, talentDto)
            talent.specialization = spec
            spec.talents += talent
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

        val maxRow =
            if ("Classic" in errorPath) 7
            else if ("TBC" in errorPath) 9
            else 11
        require(talentDto.location[0] in 0 until maxRow) {
            "Location row '${talentDto.location[0]}' out of range 0..${maxRow - 1}\n\t$errorPath"
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

        // YAML replaces newlines with spaces, which makes for some ugly double-wide spaces in the
        // displayed text. Easy fix is to just replace every sequence of 2+ spaces with a single space.
        // Also, replace one newline with 2 to make it more obvious that there is a paragraph break.
        // There might be a way to just increases the space after paragraphs in JavaFX,
        // but if there is, I don't know about it.

        // technically i should do this to every field but realistically
        // the description is the only field that will have newlines
        talent.description = talentDto.description
            .replace("\n", "\n\n")
            .replace(" {2,}".toRegex(), " ")

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
            for (costString in spellDto.cost.split("/")) {
                val match = COST_PATTERN.find(costString.trim())
                requireNotNull(match) {
                    "Spell cost must be of the form [<cost:int> <resource:Mana|Rage|Energy|" +
                        "% of Base Mana|Blood Runes?|Frost Runes?|Unholy Runes?|Runic Power>] " +
                        "(got '${spellDto.cost}')\n\t$errorPath"
                }

                val cost = match.groups["cost"]?.value?.toIntOrNull()
                    ?: throw IllegalStateException("failed to parse resource cost despite matching regex")

                val resource = match.groups["resource"]?.value
                    ?.lowercase()
                    ?.replace("\\s+".toRegex(), " ")
                val resourceType = when (resource) {
                    "mana" -> ResourceType.MANA
                    "% of base mana" -> ResourceType.PERCENT_OF_BASE_MANA
                    "energy" -> ResourceType.ENERGY
                    "rage" -> ResourceType.RAGE
                    "blood rune", "blood runes" -> ResourceType.BLOOD_RUNES
                    "frost rune", "frost runes" -> ResourceType.FROST_RUNES
                    "unholy rune", "unholy runes" -> ResourceType.UNHOLY_RUNES
                    "runic power" -> ResourceType.RUNIC_POWER
                    else -> throw IllegalStateException("failed to parse resource despite matching regex")
                }
                spell.resourceCosts.add(cost to resourceType)
            }
        }

        if (spellDto.range != null) {
            val match = RANGE_PATTERN.find(spellDto.range)
            requireNotNull(match) {
                "Spell range must be of the form [<keyword:Self|Melee>|[<minDistance:float> -] <distance:float> <unit:yd>] " +
                    "(got '${spellDto.range}')\n\t$errorPath"
            }

            spell.range = if (match.groups["keyword"] != null) {
                val keyword = match.groups["keyword"]?.value?.lowercase()
                when (keyword) {
                    "self" -> Spell.RANGE_SELF
                    "melee" -> Spell.RANGE_MELEE
                    else -> throw IllegalStateException("failed to parse range keyword despite matching regex")
                }
            } else {
                match.groups["minDistance"]?.value?.let {
                    spell.minRange = it.toDouble()
                }

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
                "Spell cast time must be of the form [<keyword:Instant|Next Melee>|<time:float> <unit:sec> [<channeled:(Channeled)>]] " +
                    "(got '${spellDto.castTime}')\n\t$errorPath"
            }

            spell.castTime = if (match.groups["keyword"] != null) {
                val keyword = match.groups["keyword"]?.value?.lowercase()?.replace("\\s+".toRegex(), " ")
                when (keyword) {
                    "instant" -> Spell.CAST_INSTANT
                    "next melee" -> Spell.CAST_NEXT_MELEE
                    else -> throw IllegalStateException("failed to parse cast time keyword despite matching regex")
                }
            } else {
                val time = match.groups["time"]?.value?.toDoubleOrNull()
                    ?: throw IllegalArgumentException("failed to parse cast time despite matching regex")

                // no need to check for unit type as it is always seconds
                requireNotNull(match.groups["unit"]) { "failed to parse cast time unit despite matching regex" }
                time
            }

            spell.isChanneled = (match.groups["isChanneled"] != null)
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
            "$ASSETS_ROOT/images/$path"
        }

        val resource = javaClass.getResourceAsStream(actualPath)
        require(resource != null) {
            "Could not find image at '$path' (absolute='$actualPath')\n\t$errorPath"
        }
        return Image(resource)
    }
}