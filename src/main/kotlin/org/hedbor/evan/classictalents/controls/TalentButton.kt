package org.hedbor.evan.classictalents.controls

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.layout.StackPane
import org.hedbor.evan.classictalents.styles.TalentButtonStyles
import org.hedbor.evan.classictalents.talents.Talent
import tornadofx.*
import java.util.*


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
            Tooltip.install(this, TalentButtonTooltip(talent, messages))
        }
        alignment = Pos.BOTTOM_RIGHT
        label(talent.allocatedPointsProperty().stringBinding { "$it" }) {
            addClass(TalentButtonStyles.pointCounter)
            padding = insets(3, 0)
            isMouseTransparent = true
        }
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
}

fun EventTarget.talentbutton(talent: Talent, messages: ResourceBundle, op: TalentButton.() -> Unit = {}) =
    opcr(this, TalentButton(talent, messages).apply(op))
