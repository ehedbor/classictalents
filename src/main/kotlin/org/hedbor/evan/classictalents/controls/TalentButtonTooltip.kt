package org.hedbor.evan.classictalents.controls

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import org.hedbor.evan.classictalents.talents.ResourceType
import org.hedbor.evan.classictalents.talents.SpellInfo
import org.hedbor.evan.classictalents.talents.Talent
import org.hedbor.evan.classictalents.util.getAndFormat
import tornadofx.*
import java.util.*
import kotlin.math.max
import org.hedbor.evan.classictalents.styles.TalentButtonTooltipStyles as Styles


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
            addClass(Styles.tooltipTitle)
            text = messages[talent.key]
        }
        label {
            addClass(Styles.tooltipSubtitle)
            textProperty().bind(talent.allocatedPointsProperty().stringBinding {
                messages.getAndFormat("talent.rank", it!!, talent.maxRank)
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
            addClass(Styles.tooltipSubtitle)
            val key = "spell.cost." + when (resourceType) {
                ResourceType.MANA -> "mana"
                ResourceType.PERCENT_OF_BASE_MANA -> "percent_of_base_mana"
                ResourceType.RAGE -> "rage"
                ResourceType.ENERGY -> "energy"
            }
            text = messages.getAndFormat(key, resourceCost)
            prefWidth = PREF_WIDTH / 2.0
        }
    }

    private fun EventTarget.generateSpellRange(rangeYds: Int, shouldAlignLeft: Boolean) {
        label {
            addClass(Styles.tooltipSubtitle)
            text = if (rangeYds == 0) {
                messages["spell.range.melee"]
            } else {
                messages.getAndFormat("spell.range.yd", rangeYds)
            }
            prefWidth = PREF_WIDTH / 2.0
            alignment = if (shouldAlignLeft) Pos.CENTER_LEFT else Pos.CENTER_RIGHT
        }
    }

    private fun EventTarget.generateSpellCastTime(castTimeSec: Int, isCasterSpell: Boolean) {
        label {
            addClass(Styles.tooltipSubtitle)
            text = if (castTimeSec == 0) {
                if (isCasterSpell) {
                    messages["spell.cast.instant_cast"]
                } else {
                    messages["spell.cast.instant"]
                }
            } else {
                messages.getAndFormat("spell.cast.sec", castTimeSec)
            }
            prefWidth = PREF_WIDTH / 2.0
        }
    }

    private fun EventTarget.generateSpellCooldown(cooldownSec: Int) {
        label {
            addClass(Styles.tooltipSubtitle)
            text = when {
                cooldownSec < 60 -> messages.getAndFormat("spell.cooldown.sec", cooldownSec)
                cooldownSec < 60 * 60 -> messages.getAndFormat("spell.cooldown.min", cooldownSec / 60.0)
                else -> messages.getAndFormat("spell.cooldown.hr", cooldownSec / 60.0 / 60.0)
            }
            prefWidth = PREF_WIDTH / 2.0
            alignment = Pos.CENTER_RIGHT
        }
    }

    private fun EventTarget.generateDescription() {
        label {
            addClass(Styles.tooltipDescription)
            textProperty().bind(talent.allocatedPointsProperty().stringBinding {
                messages.getAndFormat("${talent.key}.desc", max(1, it!!))
            })
            isWrapText = true
            prefWidth = PREF_WIDTH
        }

        if (talent.maxRank > 1) {
            label {
                addClass(Styles.tooltipSubtitle)
                text = messages["talent.rank.next"]
                visibleWhen {
                    talent.allocatedPointsProperty().booleanBinding { it in 1 until talent.maxRank }
                }
                managedWhen(visibleProperty())
            }
            label {
                addClass(Styles.tooltipDescription)
                textProperty().bind(talent.allocatedPointsProperty().stringBinding {
                    messages.getAndFormat("${talent.key}.desc", it!! + 1)
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
        label {
            addClass(Styles.tooltipError)
            val specName = messages[talent.tree.key]
            text = messages.getAndFormat("talent.requires.spec", talent.requiredPoints, specName)

            visibleWhen { !talent.talentRowUnlocked }
            managedWhen(visibleProperty())
        }

        val prereq = talent.prerequisite
        if (prereq != null) {
            label {
                addClass(Styles.tooltipError)
                val prereqName = messages[prereq.key]
                text = messages.getAndFormat("talent.requires.talent", prereq.maxRank, prereqName)

                visibleWhen { prereq.allocatedPointsProperty().booleanBinding { it!! < prereq.maxRank } }
                managedWhen(visibleProperty())
            }
        }

        label {
            addClass(Styles.tooltipConfirmation)
            textProperty().bind(talent.allocatedPointsProperty().stringBinding { points ->
                if (points == talent.maxRank) {
                    messages["talent.unlearn"]
                } else {
                    messages["talent.learn"]
                }
            })

            // TODO: make this invisible if the talent cant be unlearned
            visibleWhen { talent.shouldBeActive }
            managedWhen(visibleProperty())
        }
    }
}