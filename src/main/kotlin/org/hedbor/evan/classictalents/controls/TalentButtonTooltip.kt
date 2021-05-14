package org.hedbor.evan.classictalents.controls

import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import org.hedbor.evan.classictalents.styles.TalentButtonStyles
import org.hedbor.evan.classictalents.talents.ResourceType
import org.hedbor.evan.classictalents.talents.Talent
import org.hedbor.evan.classictalents.util.getAndFormat
import tornadofx.*
import java.util.*
import kotlin.math.max


class TalentButtonTooltip(val talent: Talent, val messages: ResourceBundle) : Tooltip() {
    init {
        isWrapText = true
        prefWidth = 400.0
        maxHeight = 500.0

        graphic = VBox().apply {
            prefWidth = 400.0
            minHeight = Region.USE_PREF_SIZE
            maxHeight = 500.0
            label {
                addClass(TalentButtonStyles.tooltipTitle)
                text = messages[talent.key]
            }
            label {
                addClass(TalentButtonStyles.tooltipSubtitle)
                textProperty().bind(talent.allocatedPointsProperty().stringBinding {
                    messages.getAndFormat("talent.rank", it!!, talent.maxRank)
                })
            }

            if (talent.spellInfo != null) {
                val info = talent.spellInfo
                hbox {
                    if (info.resourceType != null) {
                        label {
                            addClass(TalentButtonStyles.tooltipSubtitle)
                            val key = "spell.cost." + when (info.resourceType) {
                                ResourceType.MANA -> "mana"
                                ResourceType.PERCENT_OF_BASE_MANA -> "percent_of_base_mana"
                                ResourceType.RAGE -> "rage"
                                ResourceType.ENERGY -> "energy"
                            }
                            text = messages.getAndFormat(key, info.resourceCost!!)
                            prefWidth = 200.0
                        }
                    }
                    if (info.rangeYds != null) {
                        label {
                            addClass(TalentButtonStyles.tooltipSubtitle)
                            text = if (info.rangeYds == 0) {
                                messages["spell.range.melee"]
                            } else {
                                messages.getAndFormat("spell.range.yd", info.rangeYds)
                            }
                            prefWidth = 200.0
                            alignment = if (info.resourceType == null) Pos.CENTER_LEFT else Pos.CENTER_RIGHT
                        }
                    }
                }
                hbox {
                    label {
                        addClass(TalentButtonStyles.tooltipSubtitle)
                        text = if (info.castTimeSec == 0) {
                            if (info.resourceType == null) {
                                messages["spell.cast.instant"]
                            } else {
                                messages["spell.cast.instant_cast"]
                            }
                        } else {
                            messages.getAndFormat("spell.cast.sec", info.castTimeSec)
                        }
                        prefWidth = 200.0
                    }
                    if (info.cooldownSec != null) {
                        label {
                            addClass(TalentButtonStyles.tooltipSubtitle)
                            text = when {
                                info.cooldownSec < 60 -> messages.getAndFormat("spell.cooldown.sec", info.cooldownSec)
                                info.cooldownSec < 60 * 60 -> messages.getAndFormat("spell.cooldown.min", info.cooldownSec / 60.0)
                                else -> messages.getAndFormat("spell.cooldown.hr", info.cooldownSec / 60.0 / 60.0)
                            }
                            prefWidth = 200.0
                            alignment = Pos.CENTER_RIGHT
                        }
                    }
                }
            }

            label {
                addClass(TalentButtonStyles.tooltipDescription)
                textProperty().bind(talent.allocatedPointsProperty().stringBinding {
                    messages.getAndFormat("${talent.key}.desc", max(1, it!!))
                })
                isWrapText = true
                prefWidth = 400.0
            }

            if (talent.maxRank > 1) {
                label {
                    addClass(TalentButtonStyles.tooltipSubtitle)
                    text = messages["talent.rank.next"]
                    visibleWhen {
                        talent.allocatedPointsProperty().booleanBinding { it in 1 until talent.maxRank }
                    }
                    managedWhen(visibleProperty())
                }
                label {
                    addClass(TalentButtonStyles.tooltipDescription)
                    textProperty().bind(talent.allocatedPointsProperty().stringBinding {
                        messages.getAndFormat("${talent.key}.desc", it!! + 1)
                    })
                    visibleWhen {
                        talent.allocatedPointsProperty().booleanBinding { it in 1 until talent.maxRank }
                    }
                    managedWhen(visibleProperty())
                }
            }
        }
    }
}