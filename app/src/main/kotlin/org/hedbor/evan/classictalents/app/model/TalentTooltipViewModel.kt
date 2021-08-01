/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2021 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.app.model

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import org.hedbor.evan.classictalents.app.util.bindWhenNotNull
import org.hedbor.evan.classictalents.common.model.Range
import org.hedbor.evan.classictalents.common.model.ResourceType
import org.hedbor.evan.classictalents.common.model.Talent
import tornadofx.*
import kotlin.math.max
import kotlin.math.min


class TalentTooltipViewModel(private val talentButtonViewModel: TalentButtonViewModel) : ViewModel() {
    private val classKey = talentButtonViewModel.classKey
    private val specKey = talentButtonViewModel.specKey
    private val talentKey = talentButtonViewModel.talentKey

    private val rank = talentButtonViewModel.rank
    private val maxRank = talentButtonViewModel.maxRank
    private val spell = talentButtonViewModel.spell

    val talentName = talentKey.stringBinding {
        messages[talentKey.value]
    }
    val talentRank = stringBinding(rank, maxRank) {
        messages.format("talent.rank", rank.value, maxRank.value)
    }
    val talentDescription = talentKey.stringBinding(rank, maxRank) {
        val rank = min(max(Talent.MINIMUM_RANK, rank.value), maxRank.value)
        messages.format("$it.desc", rank)
    }


    val shouldShowNextRankDesc = (rank gt 0) and (rank lt maxRank)
    val nextRankTitle = messages["talent.rank.next"]!!
    val nextRankDescription = talentKey.stringBinding(rank, maxRank) {
        val nextRank = min(max(Talent.MINIMUM_RANK, rank.value + 1), maxRank.value)
        messages.format("$it.desc", nextRank)
    }

    val shouldShowRequiresSpecText = talentButtonViewModel.isTalentRowUnlocked.not()!!
    val requiresSpecText = specKey.stringBinding(talentButtonViewModel.requiredPoints) {
        messages.format("talent.requires.spec", talentButtonViewModel.requiredPoints.value, messages[it!!])
    }

    val shouldShowRequiresPrerequisiteText = talentButtonViewModel.prerequisite.isNotNull and !talentButtonViewModel.isPrerequisiteMaxedOut
    val requiresPrerequisiteText = SimpleStringProperty().bindWhenNotNull(talentButtonViewModel.prerequisite) { prereq ->
        stringBinding(prereq.maxRankProperty, specKey, prereq.translationKeyProperty) {
            val prereqKey = "${specKey.value}.${prereq.translationKey}"
            messages.format("talent.requires.talent", prereq.maxRank, messages[prereqKey])
        }
    }

    val shouldShowLearnTalentText = talentButtonViewModel.canAcceptPoints and talentButtonViewModel.hasUnassignedTalentPoints
    val learnTalentText = messages["talent.learn"]!!

    val shouldShowUnlearnTalentText = talentButtonViewModel.isDeallocatable and talentButtonViewModel.hasBeenAllocated
    val unlearnTalentText = messages["talent.unlearn"]!!


    val hasSpell = spell.isNotNull!!

    val spellCostText = SimpleStringProperty().bindWhenNotNull(spell) { spell ->
        spell.resourceTypeProperty.stringBinding(spell.resourceCostProperty) { resourceType ->
            if (resourceType == null) {
                null
            } else {
                messages.format("spell.cost", spell.resourceCost, messages[resourceType.translationKey])
            }
        }
    }

    val spellRangeText = SimpleStringProperty().bindWhenNotNull(spell) { spell ->
        spell.rangeProperty.stringBinding {
            if (Range.isMelee(spell.range)) {
                messages["spell.range.melee"]
            } else {
                messages.format("spell.range", spell.range, messages["unit.distance.yards"])
            }
        }
    }

    val spellRangeAlignment = SimpleObjectProperty<Pos>().bindWhenNotNull(spell) { spell ->
        Bindings.`when`(spell.resourceTypeProperty.isNull)
            .then(Pos.CENTER_LEFT)
            .otherwise(Pos.CENTER_RIGHT)
    }

    val spellCastTimeText = SimpleStringProperty().bindWhenNotNull(spell) { spell ->
        spell.castTimeProperty.stringBinding(spell.resourceTypeProperty) {
            val isCasterSpell = when (spell.resourceType) {
                ResourceType.MANA, ResourceType.PERCENT_OF_BASE_MANA -> true
                ResourceType.ENERGY, ResourceType.RAGE -> false
                null -> false
            }
            if (spell.castTime > 0.0) {
                messages.format("spell.cast", spell.castTime, messages["unit.time.seconds"])
            } else {
                if (isCasterSpell) {
                    messages["spell.cast.instant_cast"]
                } else {
                    messages["spell.cast.instant_attack"]
                }
            }
        }
    }

    val spellCooldownText = SimpleStringProperty().bindWhenNotNull(spell) { spell ->
        spell.cooldownUnitProperty.stringBinding(spell.cooldownProperty) { cooldownUnit ->
            if (cooldownUnit == null) null
            else messages.format("spell.cooldown", spell.cooldown, messages[cooldownUnit.translationKey])
        }
    }
}