package org.hedbor.evan.classictalents.controls

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.input.MouseButton
import javafx.scene.layout.StackPane
import org.hedbor.evan.classictalents.talents.Talent
import org.hedbor.evan.classictalents.styles.TalentButtonStyles
import tornadofx.*


class TalentButton(val talent: Talent) : StackPane() {
    companion object {
        private const val BORDER_IMAGE = "/images/Icon/large/border/default.png"
        private const val BORDER_HILITE_HOVER_IMAGE = "/images/Icon/large/hilite/hilite.png"
        private const val BORDER_HILITE_ENABLED_IMAGE = "/images/Icon/large/hilite/enabled.png"
        private const val BORDER_HILITE_MAX_RANK_IMAGE = "/images/Icon/large/hilite/max_rank.png"
    }

    var allocatedPoints: Int by property(0)
    fun allocatedPointsProperty() = getProperty(TalentButton::allocatedPoints)

    init {
        togglebutton(selectFirst = false) {
            addClass(TalentButtonStyles.talentButton)
            padding = insets(0)
            graphic = StackPane().apply {
                imageview(talent.icon)
                imageview(BORDER_IMAGE)
                imageview(BORDER_HILITE_HOVER_IMAGE) {
                    visibleProperty().bind(this@togglebutton.hoverProperty())
                }
                imageview(BORDER_HILITE_ENABLED_IMAGE) {
                    visibleProperty().bind(this@togglebutton.disableProperty().booleanBinding(allocatedPointsProperty()) { !(it!!) && allocatedPoints != talent.maxRank })
                }
                imageview(BORDER_HILITE_MAX_RANK_IMAGE) {
                    visibleProperty().bind(allocatedPointsProperty().booleanBinding { it == talent.maxRank })
                }
                children.forEach { it.addClass(TalentButtonStyles.talentButtonIcon) }
            }
            setOnMouseClicked { event ->
                if (event.button == MouseButton.PRIMARY && allocatedPoints < talent.maxRank) {
                    allocatedPoints++
                } else if (event.button == MouseButton.SECONDARY && allocatedPoints > 0) {
                    allocatedPoints--
                }
                isSelected = allocatedPoints == talent.maxRank
            }
        }
        alignment = Pos.BOTTOM_RIGHT
        label(allocatedPointsProperty().stringBinding { "$it/${talent.maxRank}" }) {
            addClass(TalentButtonStyles.pointCounter)
            padding = insets(3, 0)
            isMouseTransparent = true
        }
    }

    override fun getUserAgentStylesheet(): String = TalentButtonStyles().base64URL.toExternalForm()
}

fun EventTarget.talentbutton(talent: Talent, op: TalentButton.() -> Unit) = opcr(this, TalentButton(talent).apply(op))
