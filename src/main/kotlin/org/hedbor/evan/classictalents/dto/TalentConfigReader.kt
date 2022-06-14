package org.hedbor.evan.classictalents.dto

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.paint.Color
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.model.*
import org.hedbor.evan.classictalents.util.observableListOf
import org.hedbor.evan.classictalents.util.toObservableList

class TalentConfigReader {
    private val mapper = ObjectMapper(YAMLFactory())
    private val rootType = object : TypeReference<TalentConfigDto>() {}

    /**
     * Reads a talent file from the specified resource location.
     *
     * @param filePath a path to a resource file
     * @return the parsed [WowClass]
     * @throws IllegalStateException if an error is found while parsing
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
        wowClass.icon = Image(validateAssetFile(errorPath, classDto.icon))
        wowClass.color = try {
            Color.web(classDto.color)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Could not parse color '${classDto.color}'\n\t$errorPath", e)
        }

        for ((specName, specDto) in classDto.classic.specs) {
            wowClass.specializations += readSpec("$errorPath, spec '$specName'", specName, specDto)
        }

        return wowClass
    }

    private fun readSpec(errorPath: String, specName: String, specDto: SpecDto): Specialization {
        val spec = Specialization()
        spec.name = specName
        spec.icon = Image(validateAssetFile(errorPath, specDto.icon))
        spec.background = Image(validateAssetFile(errorPath, specDto.background))

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
            TODO()
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

        talent.icon = Image(validateAssetFile(errorPath, talentDto.icon))

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
            val costString = spellDto.cost.replace("\\s+".toRegex(), "").lowercase()

            val errorMsg = {
                "Spell cost must be of the form [<cost:int> <Mana|Rage|Energy|% of Base Mana>] " +
                    "(got '${spellDto.cost}')\n\t$errorPath"
            }

            val costMatch = Regex("^\\d+").find(costString)
            require(costMatch != null, errorMsg)
            spell.cost = costMatch.value.toInt()

            val resourcePart = costString.substring(costMatch.range.last + 1)
            spell.resource = when (resourcePart) {
                "mana" -> ResourceType.MANA
                "%ofbasemana" -> ResourceType.PERCENT_OF_BASE_MANA
                "energy" -> ResourceType.ENERGY
                "rage" -> ResourceType.RAGE
                else -> throw IllegalStateException(errorMsg())
            }
        }

        if (spellDto.range != null) {
            val rangeString = spellDto.range.replace("\\s+".toRegex(), "").lowercase()
            spell.range = when (rangeString) {
                "self" -> 0.0
                "melee" -> 5.0
                else -> {
                    val errorMsg = {
                        "Spell range must be of the form [Self|Melee|<distance:float> yd] " +
                            "(got '${spellDto.range}')\n\t$errorPath"
                    }

                    val distanceMatch = Regex("^\\d+(\\.\\d+)?").find(rangeString)
                    require(distanceMatch != null, errorMsg)

                    val unit = rangeString.substring(distanceMatch.range.last + 1)
                    require(unit in arrayOf("yd", "yds", "yard", "yards"), errorMsg)

                    distanceMatch.value.toDouble()
                }
            }
        }

        val castTimeString = spellDto.castTime.replace("\\s+", "").lowercase()
        spell.castTime = if (castTimeString == "instant") {
            0.0
        } else {
            val errorMsg = {
                "Spell cast time must be of the form [Instant|<time:float> sec] " +
                    "(got '${spellDto.range}')\n\t$errorPath"
            }

            val timeMatch = Regex("^\\d+(\\.\\d+)?").find(castTimeString)
            require(timeMatch != null, errorMsg)

            val unit = castTimeString.substring(timeMatch.range.last + 1)
            require(unit in arrayOf("sec", "secs", "second", "seconds"), errorMsg)

            timeMatch.value.toDouble()
        }

        if (spellDto.cooldown != null) {
            val cooldownString = spellDto.cooldown.replace("\\s+", "").lowercase()
            val errorMsg = {
                "Spell cooldown must be of the form [<time:float> <sec|min|hr>] " +
                    "(got '${spellDto.range}')\n\t$errorPath"
            }

            val cooldownMatch = Regex("^\\d+(\\.\\d+)?").find(cooldownString)
            require(cooldownMatch != null, errorMsg)
            spell.cooldown = cooldownMatch.value.toDouble()

            val unit = cooldownString.substring(cooldownMatch.range.last + 1)
            spell.cooldownUnit = when (unit) {
                "sec", "secs", "second", "seconds" -> CooldownUnit.SECONDS
                "min", "mins", "minute", "minutes" -> CooldownUnit.MINUTES
                "hr", "hrs", "hour", "hours" -> CooldownUnit.HOURS
                else -> throw IllegalStateException(errorMsg())
            }
        }

        return spell
    }

    private fun validateAssetFile(errorPath: String, path: String): String {
        val actualPath = if (path.startsWith("/")) {
            path
        } else {
            "$ASSETS_ROOT/$path"
        }
        require(javaClass.getResource(path) != null) {
            "Could not find asset at '$path' (absolute='$actualPath')\n\t$errorPath"
        }
        return actualPath
    }
}