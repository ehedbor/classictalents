package org.hedbor.evan.classictalents.app.view

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import org.hedbor.evan.classictalents.app.model.ResourceType
import org.hedbor.evan.classictalents.app.model.SpellInfo
import org.hedbor.evan.classictalents.app.model.Talent
import org.hedbor.evan.classictalents.app.view.styles.TalentTooltipStyles
import tornadofx.*
import java.util.*
import kotlin.math.max


class TalentButtonTooltip(private val talent: Talent, private val messages: ResourceBundle) : Tooltip() {
    companion object {
        private const val PREF_WIDTH = 400.0
        private const val PREF_HEIGHT = 600.0
    }

    init {
        isWrapText = true
        prefWidth = PREF_WIDTH
        maxHeight = PREF_HEIGHT

        graphic = VBox().apply {
            prefWidth = PREF_WIDTH
            minHeight = Region.USE_PREF_SIZE
            maxHeight = PREF_HEIGHT

            generateHeader()
            if (talent.spellInfo != null) {
                generateSpellInfo(talent.spellInfo)
            }
            generateDescription()
            generateFooter()

        }
    }

    private fun EventTarget.generateHeader() {
        label {
            addClass(TalentTooltipStyles.tooltipTitle)
            text = messages[talent.key]
        }
        label {
            addClass(TalentTooltipStyles.tooltipSubtitle)
            textProperty().bind(talent.allocatedPointsProperty().stringBinding {
                messages.format("talent.rank", it!!, talent.maxRank)
            })
        }
    }

    private fun EventTarget.generateSpellInfo(info: SpellInfo) {
        hbox {
            if (info.resourceType != null) {
                generateSpellResource(info.resourceType, info.resourceCost!!)
            }
            if (info.rangeYds != null) {
                generateSpellRange(info.rangeYds, info.resourceType == null)
            }
        }
        hbox {
            val isCasterSpell = info.resourceType == ResourceType.MANA || info.resourceType == ResourceType.PERCENT_OF_BASE_MANA
            generateSpellCastTime(info.castTimeSec, isCasterSpell)

            if (info.cooldownSec != null) {
                generateSpellCooldown(info.cooldownSec)
            }
        }
    }

    private fun EventTarget.generateSpellResource(resourceType: ResourceType, resourceCost: Int) {
        label {
            addClass(TalentTooltipStyles.tooltipSubtitle)
            val key = "spell.cost." + when (resourceType) {
                ResourceType.MANA -> "mana"
                ResourceType.PERCENT_OF_BASE_MANA -> "percent_of_base_mana"
                ResourceType.RAGE -> "rage"
                ResourceType.ENERGY -> "energy"
            }
            text = messages.format(key, resourceCost)
            prefWidth = PREF_WIDTH / 2.0
        }
    }

    private fun EventTarget.generateSpellRange(rangeYds: Int, shouldAlignLeft: Boolean) {
        label {
            addClass(TalentTooltipStyles.tooltipSubtitle)
            text = if (rangeYds == 0) {
                messages["spell.range.melee"]
            } else {
                messages.format("spell.range.yd", rangeYds)
            }
            prefWidth = PREF_WIDTH / 2.0
            alignment = if (shouldAlignLeft) Pos.CENTER_LEFT else Pos.CENTER_RIGHT
        }
    }

    private fun EventTarget.generateSpellCastTime(castTimeSec: Int, isCasterSpell: Boolean) {
        label {
            addClass(TalentTooltipStyles.tooltipSubtitle)
            text = if (castTimeSec == 0) {
                if (isCasterSpell) {
                    messages["spell.cast.instant_cast"]
                } else {
                    messages["spell.cast.instant"]
                }
            } else {
                messages.format("spell.cast.sec", castTimeSec)
            }
            prefWidth = PREF_WIDTH / 2.0
        }
    }

    private fun EventTarget.generateSpellCooldown(cooldownSec: Int) {
        label {
            addClass(TalentTooltipStyles.tooltipSubtitle)
            text = when {
                cooldownSec < 60 -> messages.format("spell.cooldown.sec", cooldownSec)
                cooldownSec < 60 * 60 -> messages.format("spell.cooldown.min", cooldownSec / 60.0)
                else -> messages.format("spell.cooldown.hr", cooldownSec / 60.0 / 60.0)
            }
            prefWidth = PREF_WIDTH / 2.0
            alignment = Pos.CENTER_RIGHT
        }
    }

    private fun EventTarget.generateDescription() {
        label {
            addClass(TalentTooltipStyles.tooltipDescription)
            textProperty().bind(talent.allocatedPointsProperty().stringBinding {
                messages.format("${talent.key}.desc", max(1, it!!))
            })
            isWrapText = true
            prefWidth = PREF_WIDTH
        }

        if (talent.maxRank > 1) {
            // spacer
            label {}
            label {
                addClass(TalentTooltipStyles.tooltipSubtitle)
                text = messages["talent.rank.next"]
                visibleWhen {
                    talent.allocatedPointsProperty().booleanBinding { it in 1 until talent.maxRank }
                }
                managedWhen(visibleProperty())
            }
            label {
                addClass(TalentTooltipStyles.tooltipDescription)
                textProperty().bind(talent.allocatedPointsProperty().stringBinding {
                    messages.format("${talent.key}.desc", it!! + 1)
                })
                isWrapText = true
                prefWidth = PREF_WIDTH

                visibleWhen {
                    talent.allocatedPointsProperty().booleanBinding { it in 1 until talent.maxRank }
                }
                managedWhen(visibleProperty())
            }
        }
    }

    private fun EventTarget.generateFooter() {
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
    }
}