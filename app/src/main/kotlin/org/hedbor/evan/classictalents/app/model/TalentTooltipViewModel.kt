package org.hedbor.evan.classictalents.app.model

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import org.hedbor.evan.classictalents.app.util.bindWhenNotNull
import org.hedbor.evan.classictalents.common.model.*
import tornadofx.*
import kotlin.math.max
import kotlin.math.min


class TalentTooltipViewModel(private val talentButtonViewModel: TalentButtonViewModel) : ViewModel() {
    private var wowClass: WowClass by talentButtonViewModel.wowClassProperty
    private var specialization: Specialization by talentButtonViewModel.specializationProperty
    private var talent: Talent by talentButtonViewModel.talentProperty

    private val wowClassKey = SimpleStringProperty().apply { bind(wowClass.translationKeyProperty) }
    private val specKey = wowClassKey.stringBinding(specialization.translationKeyProperty) { "$it.${specialization.translationKey}" }
    private val talentKey = specKey.stringBinding(talent.translationKeyProperty) { "$it.${talent.translationKey}" }


    val talentName = talentKey.stringBinding {
        messages[talentKey.value]
    }
    val talentRank = talent.rankProperty.stringBinding(talent.maxRankProperty) {
        messages.format("talent.rank", talent.rank, talent.maxRank)
    }
    val talentDescription = talentKey.stringBinding(talent.rankProperty, talent.maxRankProperty) {
        val rank = min(max(Talent.MINIMUM_RANK, talent.rank), talent.maxRank)
        messages.format("$it.desc", rank)
    }


    val shouldShowNextRankDesc = (talent.rankProperty gt 0) and (talent.rankProperty lt talent.maxRankProperty)
    val nextRankTitle = messages["talent.rank.next"]!!
    val nextRankDescription = talentKey.stringBinding(talent.rankProperty, talent.maxRankProperty) {
        val nextRank = min(max(Talent.MINIMUM_RANK, talent.rank + 1), talent.maxRank)
        messages.format("$it.desc", nextRank)
    }

    val shouldShowRequiresSpecText = talentButtonViewModel.isTalentRowUnlocked.not()
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

    val shouldShowLearnTalentText = talentButtonViewModel.isAllocatable
    val learnTalentText = messages["talent.learn"]!!

    val shouldShowUnlearnTalentText = talentButtonViewModel.isDeallocatable and talentButtonViewModel.hasBeenAllocated
    val unlearnTalentText = messages["talent.unlearn"]!!


    val hasSpell = talent.spellProperty.isNotNull!!

    val spellCostText = SimpleStringProperty().bindWhenNotNull(talent.spellProperty) { spell ->
        spell.resourceTypeProperty.stringBinding(spell.resourceCostProperty) { resourceType ->
            if (resourceType == null) {
                null
            } else {
                messages.format("spell.cost", spell.resourceCost, messages[resourceType.translationKey])
            }
        }
    }

    val spellRangeText = SimpleStringProperty().bindWhenNotNull(talent.spellProperty) { spell ->
        spell.rangeProperty.stringBinding {
            if (Range.isMelee(spell.range)) {
                messages["spell.range.melee"]
            } else {
                messages.format("spell.range", spell.range, messages["unit.distance.yards"])
            }
        }
    }

    val spellRangeAlignment = SimpleObjectProperty<Pos>().bindWhenNotNull(talent.spellProperty) { spell ->
        Bindings.`when`(spell.resourceTypeProperty.isNull)
            .then(Pos.CENTER_LEFT)
            .otherwise(Pos.CENTER_RIGHT)
    }

    val spellCastTimeText = SimpleStringProperty().bindWhenNotNull(talent.spellProperty) { spell ->
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

    val spellCooldownText = SimpleStringProperty().bindWhenNotNull(talent.spellProperty) { spell ->
        spell.cooldownUnitProperty.stringBinding(spell.cooldownProperty) { cooldownUnit ->
            if (cooldownUnit == null) null
            else messages.format("spell.cooldown", spell.cooldown, messages[cooldownUnit.translationKey])
        }
    }
}