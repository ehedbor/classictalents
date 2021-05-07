package org.hedbor.evan.classictalents.controls

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.layout.StackPane
import javafx.scene.text.Font
import org.hedbor.evan.classictalents.talents.Talent
import org.hedbor.evan.classictalents.styles.TalentButtonStyles
import org.hedbor.evan.classictalents.talents.ResourceType
import org.hedbor.evan.classictalents.util.getAndFormat
import tornadofx.*
import java.util.*
import kotlin.math.max


class TalentButton(val talent: Talent, val messages: ResourceBundle) : StackPane() {
    companion object {
        private const val BORDER_IMAGE = "/images/Icon/large/border/default.png"
        private const val BORDER_HILITE_HOVER_IMAGE = "/images/Icon/large/hilite/hilite.png"
        private const val BORDER_HILITE_ENABLED_IMAGE = "/images/Icon/large/hilite/enabled.png"
        private const val BORDER_HILITE_MAX_RANK_IMAGE = "/images/Icon/large/hilite/max_rank.png"
    }

    @Suppress("JoinDeclarationAndAssignment")
    private val button: ToggleButton

    // used to compute min/max/pref size
    private val borderImage = Image(BORDER_IMAGE)

    init {
        button = togglebutton(selectFirst = false) {
            addClass(TalentButtonStyles.talentButton)
            padding = insets(0)
            graphic = StackPane().apply {
                imageview(talent.icon)
                imageview(borderImage)
                imageview(BORDER_HILITE_HOVER_IMAGE) {
                    visibleProperty().bind(this@togglebutton.hoverProperty())
                }
                imageview(BORDER_HILITE_ENABLED_IMAGE) {
                    visibleProperty().bind(talent.allocatedPointsProperty().booleanBinding(this@togglebutton.disableProperty()) { allocatedPoints ->
                        allocatedPoints != talent.maxRank && !this@togglebutton.isDisable
                    })
                }
                imageview(BORDER_HILITE_MAX_RANK_IMAGE) {
                    visibleProperty().bind(talent.allocatedPointsProperty().booleanBinding { it == talent.maxRank })
                }
                children.forEach { it.addClass(TalentButtonStyles.talentButtonIcon) }
            }
            setOnMouseClicked { event ->
                if (event.button == MouseButton.PRIMARY && talent.allocatedPoints < talent.maxRank) {
                    talent.allocatedPoints++
                } else if (event.button == MouseButton.SECONDARY && talent.allocatedPoints > 0) {
                    talent.allocatedPoints--
                }
                isSelected = talent.allocatedPoints == talent.maxRank
            }
        }
        alignment = Pos.BOTTOM_RIGHT
        label(talent.allocatedPointsProperty().stringBinding { "$it/${talent.maxRank}" }) {
            addClass(TalentButtonStyles.pointCounter)
            padding = insets(3, 0)
            isMouseTransparent = true
        }
        tooltip {
            prefWidth = 500.0
            // TODO: figure out a way to allow enough space for all the text
            maxHeight = 400.0
            isWrapText = true
            font = Font.font("Arial", 12.0)
            
            textProperty().bind(talent.allocatedPointsProperty().stringBinding { computeTooltip() })
        }
    }

    private fun computeTooltip(): String {
        /*
         * (Talent name)           Rank #
         * Talent
         * Requires Class (Spec)
         * Current Description
         *
         * Next rank:
         * Next rank description
         *
         * Click to learn
         */
        return StringBuilder().apply {
            appendLine(messages[talent.key])
            appendLine(messages["talent"])
            // TODO: check if it is a spell
            appendLine(messages.getAndFormat("talent.rank", max(1, talent.allocatedPoints)))
            appendLine(messages.getAndFormat("talent.requires.class", "Class", "Spec"))
            appendLine(messages.getAndFormat("${talent.key}.desc", max(1, talent.allocatedPoints)))

            if (talent.spellInfo != null) {
                val info = talent.spellInfo
                // mana range cast cooldown
                if (info.resourceType != null) {
                    val key = "spell.cost." + when (info.resourceType) {
                        ResourceType.MANA -> "mana"
                        ResourceType.PERCENT_OF_BASE_MANA -> "percent_of_base_mana"
                        ResourceType.RAGE -> "rage"
                        ResourceType.ENERGY -> "energy"
                    }
                    appendLine(messages.getAndFormat(key, info.resourceCost!!))
                }

                if (info.rangeYds != null) {
                    if (info.rangeYds == 0) {
                        appendLine(messages["spell.range.melee"])
                    } else {
                        appendLine(messages.getAndFormat("spell.range.yd", info.rangeYds))
                    }
                }

                if (info.castTimeSec == 0) {
                    appendLine(messages["spell.cast.instant"])
                } else {
                    appendLine(messages.getAndFormat("spell.cast.sec", info.castTimeSec))
                }

                if (info.cooldownSec != null) {
                    when {
                        info.cooldownSec > 60 -> {
                            appendLine(messages.getAndFormat("spell.cooldown.sec", info.cooldownSec))
                        }
                        info.cooldownSec > 60 * 60 -> {
                            appendLine(messages.getAndFormat("spell.cooldown.min", info.cooldownSec / 60.0))
                        }
                        else -> {
                            appendLine(messages.getAndFormat("spell.cooldown.hr", info.cooldownSec / 60.0 / 60.0))
                        }
                    }
                }
            }
            
            if (talent.allocatedPoints in 1 until talent.maxRank) {
                appendLine()
                appendLine(messages["talent.rank.next"])
                appendLine(messages.getAndFormat("${talent.key}.desc", talent.allocatedPoints + 1))
            }
            appendLine()

            // TODO: check for spec/talent requirements
            if (talent.allocatedPoints < talent.maxRank)
                appendLine(messages["talent.learn"])
            else
                appendLine(messages["talent.unlearn"])
        }.toString()
    }

    override fun computeMinWidth(height: Double): Double {
        return computePrefWidth(height)
    }

    override fun computeMinHeight(width: Double): Double {
        return computePrefHeight(width)
    }

    override fun computePrefWidth(height: Double): Double {
        return borderImage.width + snappedLeftInset() + snappedRightInset()
    }

    override fun computePrefHeight(width: Double): Double {
        return borderImage.height + snappedTopInset() + snappedBottomInset()
    }

    override fun computeMaxWidth(height: Double): Double {
        return  computePrefWidth(height)
    }

    override fun computeMaxHeight(width: Double): Double {
        return computePrefHeight(width)
    }

    override fun getUserAgentStylesheet(): String = TalentButtonStyles().base64URL.toExternalForm()
}

fun EventTarget.talentbutton(talent: Talent, messages: ResourceBundle, op: TalentButton.() -> Unit = {}) =
    opcr(this, TalentButton(talent, messages).apply(op))
