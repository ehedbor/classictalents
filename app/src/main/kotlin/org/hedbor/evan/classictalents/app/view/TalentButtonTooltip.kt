package org.hedbor.evan.classictalents.app.view

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import org.hedbor.evan.classictalents.app.view.styles.TalentTooltipStyles
import org.hedbor.evan.classictalents.common.model.CooldownUnit
import org.hedbor.evan.classictalents.common.model.Range
import org.hedbor.evan.classictalents.common.model.ResourceType
import org.hedbor.evan.classictalents.common.model.Talent
import tornadofx.*
import kotlin.math.max


class TalentButtonTooltip(private val talent: Talent) : Tooltip() {
    companion object {
        private const val PREF_WIDTH = 400.0
        private const val PREF_HEIGHT = 600.0
    }

    private val messages get() = FX.messages

    init {
        isWrapText = true
        prefWidth = PREF_WIDTH
        maxHeight = PREF_HEIGHT

        graphic = VBox().apply {
            prefWidth = PREF_WIDTH
            minHeight = Region.USE_PREF_SIZE
            maxHeight = PREF_HEIGHT

            generateHeader()
            generateSpellInfo()
            generateDescription()
            generateFooter()
        }
    }

    private fun EventTarget.generateHeader() {
        label {
            addClass(TalentTooltipStyles.tooltipTitle)
            text = messages[talent.translationKey]
        }
        label {
            addClass(TalentTooltipStyles.tooltipSubtitle)
            textProperty().bind(talent.rankProperty.stringBinding(talent.maxRankProperty) {
                messages.format("talent.rank", it!!, talent.maxRank)
            })
        }
    }

    private fun EventTarget.generateSpellInfo() {
        val spell = talent.spell ?: return
        hbox {
            if (spell.resourceType != null) {
                generateSpellResource(spell.resourceCost, spell.resourceType!!)
            }
            if (!Range.isSelf(spell.range)) {
                generateSpellRange(spell.range, spell.resourceType == null)
            }
        }
        hbox {
            generateSpellCastTime(spell.castTime, spell.resourceType)

            if (spell.cooldownUnit != null) {
                generateSpellCooldown(spell.cooldown, spell.cooldownUnit!!)
            }
        }
    }

    private fun EventTarget.generateSpellResource(resourceCost: Int, resourceType: ResourceType) {
        label {
            addClass(TalentTooltipStyles.tooltipSubtitle)
            text = messages.format("spell.cost", resourceCost, messages[resourceType.translationKey])
            prefWidth = PREF_WIDTH / 2.0
        }
    }

    private fun EventTarget.generateSpellRange(range: Double, shouldAlignLeft: Boolean) {
        label {
            addClass(TalentTooltipStyles.tooltipSubtitle)
            text = if (Range.isMelee(range)) {
                messages["spell.range.melee"]
            } else {
                messages.format("spell.range", range, messages["unit.distance.yards"])
            }
            prefWidth = PREF_WIDTH / 2.0
            alignment = if (shouldAlignLeft) Pos.CENTER_LEFT else Pos.CENTER_RIGHT
        }
    }

    private fun EventTarget.generateSpellCastTime(castTime: Double, resourceType: ResourceType?) {
        label {
            addClass(TalentTooltipStyles.tooltipSubtitle)

            val isCasterSpell = when (resourceType) {
                ResourceType.MANA, ResourceType.PERCENT_OF_BASE_MANA -> true
                ResourceType.RAGE, ResourceType.ENERGY -> false
                null -> false
            }
            text = if (castTime == 0.0) {
                if (isCasterSpell) {
                    messages["spell.cast.instant_cast"]
                } else {
                    messages["spell.cast.instant_attack"]
                }
            } else {
                messages.format("spell.cast", castTime, messages["unit.time.seconds"])
            }
            prefWidth = PREF_WIDTH / 2.0
        }
    }

    private fun EventTarget.generateSpellCooldown(cooldown: Double, cooldownUnit: CooldownUnit) {
        label {
            addClass(TalentTooltipStyles.tooltipSubtitle)
            text = messages.format("spell.cooldown", cooldown, messages[cooldownUnit.translationKey])
            prefWidth = PREF_WIDTH / 2.0
            alignment = Pos.CENTER_RIGHT
        }
    }

    private fun EventTarget.generateDescription() {
        // show the current rank's description (or rank 1 if the current rank is 0)
        label {
            addClass(TalentTooltipStyles.tooltipDescription)
            textProperty().bind(talent.rankProperty.stringBinding {
                messages.format("${talent.translationKey}.desc", max(1, talent.rank))
            })
            isWrapText = true
            prefWidth = PREF_WIDTH
        }

        // show the next rank's description when at least 1 point is allocated,
        // but not at the max rank yet
        if (talent.maxRank > 1) {
            // spacer
            label {}
            label {
                addClass(TalentTooltipStyles.tooltipSubtitle)
                text = messages["talent.rank.next"]
                visibleWhen {
                    talent.rankProperty.booleanBinding { talent.rank in 1 until talent.maxRank }
                }
                managedWhen(visibleProperty())
            }
            label {
                addClass(TalentTooltipStyles.tooltipDescription)
                textProperty().bind(talent.rankProperty.stringBinding {
                    messages.format("${talent.translationKey}.desc", talent.rank + 1)
                })
                isWrapText = true
                prefWidth = PREF_WIDTH

                visibleWhen {
                    talent.rankProperty.booleanBinding { talent.rank in 1 until talent.maxRank }
                }
                managedWhen(visibleProperty())
            }
        }
    }

    private fun EventTarget.generateFooter() {
        /*
        // spacer
        label {}

        label {
            addClass(TalentTooltipStyles.tooltipError)
            val specName = messages[talent.tree.key]
            text = messages.format("talent.requires.spec", talent.requiredPoints, specName)

            visibleWhen { !talent.talentRowUnlocked }
            managedWhen(visibleProperty())
        }

        val prereq = talent.prerequisite
        if (prereq != null) {
            label {
                addClass(TalentTooltipStyles.tooltipError)
                val prereqName = messages[prereq.key]
                text = messages.format("talent.requires.talent", prereq.maxRank, prereqName)

                visibleWhen { prereq.allocatedPointsProperty().booleanBinding { it!! < prereq.maxRank } }
                managedWhen(visibleProperty())
            }
        }

        label {
            addClass(TalentTooltipStyles.tooltipConfirmation)
            text = messages["talent.learn"]
            visibleWhen { talent.shouldBeActive }
            managedWhen(visibleProperty())
        }

        label {
            addClass(TalentTooltipStyles.tooltipError)
            text = messages["talent.unlearn"]
            visibleWhen { talent.canRemovePoints }
            managedWhen(visibleProperty())
        }
        */
    }
}