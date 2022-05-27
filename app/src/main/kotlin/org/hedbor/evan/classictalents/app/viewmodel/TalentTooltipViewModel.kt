/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2022 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.app.viewmodel

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import org.hedbor.evan.classictalents.app.util.bindWhenNotNull
import org.hedbor.evan.classictalents.common.model.ResourceType
import org.hedbor.evan.classictalents.common.model.TalentData
import tornadofx.*
import kotlin.math.max
import kotlin.math.min


@Suppress("HasPlatformType")
class TalentTooltipViewModel(private val props: TalentProperties) : ViewModel() {
    private val talent = props.talent
    
    val talentName = messages[props.fullKey]
    val talentRank = talent.rankProperty.stringBinding { rank ->
        messages.format("talent.rank", rank as Int, talent.maxRank)
    }
    val talentDescription = talent.rankProperty.stringBinding { rank ->
        val clampedRank = min(max(TalentData.MINIMUM_RANK, rank as Int), talent.maxRank)
        messages.format("${props.fullKey}.desc", clampedRank)
    }

    val hasNextRank = props.hasBeenAllocated and props.isMaxedOut.not()
    val nextRankTitle = messages["talent.rank.next"]
    val nextRankDescription = talent.rankProperty.stringBinding { rank ->
        val nextRank = min(max(TalentData.MINIMUM_RANK, rank as Int + 1), talent.maxRank)
        messages.format("${props.fullKey}.desc", nextRank)
    }

    val requiresSpec = props.isTalentRowUnlocked.not()
    val requiresSpecText = messages.format("talent.requires.spec", props.requiredPoints, messages[props.specKey])

    val requiresPrerequisite = props.isPrerequisiteMaxedOut.not()
    val requiresPrerequisiteText = SimpleStringProperty().bindWhenNotNull(props.prerequisite) { prereq ->
        SimpleStringProperty(run {
            val prereqKey = "${props.specKey}.${prereq.translationKey}"
            messages.format("talent.requires.talent", prereq.maxRank, messages[prereqKey])
        })
    }

    val canLearnTalent = props.canAcceptPoints and props.hasUnassignedTalentPoints
    val learnTalentText = messages["talent.learn"]

    val canUnlearnTalent = props.isDeallocatable and props.hasBeenAllocated
    val unlearnTalentText = messages["talent.unlearn"]

    val hasSpell = SimpleBooleanProperty(talent.spell != null)

    val spellCostText = if (talent.spell != null) {
        val resourceCost = talent.spell!!.resourceCost
        val resourceType = talent.spell!!.resourceType
        if (resourceType != null)
            messages.format("spell.cost", resourceCost, messages[resourceType.translationKey])
        else ""
    } else ""
    val hasSpellCost = SimpleBooleanProperty(spellCostText.isNotBlank())

    val spellRangeText = if (talent.spell != null) {
        val range = talent.spell!!.range
        if (range.isMelee) {
            messages["spell.range.melee"]
        } else {
            messages.format("spell.range", range, messages["unit.distance.yards"])
        }
    } else ""
    val hasSpellRange = SimpleBooleanProperty(spellRangeText.isNotBlank())

    val spellRangeAlignment = if (talent.spell != null) {
        if (talent.spell?.resourceType == null)
            Pos.CENTER_LEFT
        else
            Pos.CENTER_RIGHT
    } else null

    val spellCastTimeText = if (talent.spell != null) {
        val isCasterSpell = when (talent.spell!!.resourceType) {
            ResourceType.MANA, ResourceType.PERCENT_OF_BASE_MANA -> true
            ResourceType.ENERGY, ResourceType.RAGE -> false
            null -> false
        }
        val castTime = talent.spell!!.castTime
        if (castTime > 0.0) {
            messages.format("spell.cast", castTime, messages["unit.time.seconds"])
        } else {
            if (isCasterSpell) {
                messages["spell.cast.instant_cast"]
            } else {
                messages["spell.cast.instant_attack"]
            }
        }
    } else ""
    val hasSpellCastTime = SimpleBooleanProperty(spellCastTimeText.isNotBlank())

    val spellCooldownText = if (talent.spell != null) {
        val cooldownUnit = talent.spell!!.cooldownUnit
        val cooldown = talent.spell!!.cooldown
        if (cooldownUnit == null) ""
        else messages.format("spell.cooldown", cooldown, messages[cooldownUnit.translationKey])
    } else ""
    val hasSpellCooldown = SimpleBooleanProperty(spellCooldownText.isNotBlank())
}