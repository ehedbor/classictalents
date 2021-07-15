package org.hedbor.evan.classictalents.app.view

import javafx.event.EventTarget
import javafx.scene.layout.StackPane
import org.hedbor.evan.classictalents.app.model.TalentButtonViewModel
import org.hedbor.evan.classictalents.app.view.styles.SpecStyles
import org.hedbor.evan.classictalents.common.model.Specialization
import org.hedbor.evan.classictalents.common.model.Talent
import org.hedbor.evan.classictalents.common.model.WowClass
import tornadofx.*
import java.net.URI


// TODO: Extend Fragment() and use a ViewModel
class TalentTreeView(private val wowClass: WowClass, private val spec: Specialization) : StackPane() {
    companion object {
        private const val BUTTON_INSETS = 15.0
        // The button textures have blank pixels along the edges. This alignment variable accounts for that
        private const val ARROW_ALIGN = 2.0
        private const val ARROW_THICKNESS = 5.0

        private const val DOWN_ARROW              = "/images/WowheadTalentCalc/arrows/down.png"
        private const val DOWN_ARROW_HILITE       = "/images/WowheadTalentCalc/arrows/down2.png"
        private const val LEFT_ARROW              = "/images/WowheadTalentCalc/arrows/left.png"
        private const val LEFT_ARROW_HILITE       = "/images/WowheadTalentCalc/arrows/left2.png"
        private const val LEFT_DOWN_ARROW         = "/images/WowheadTalentCalc/arrows/leftdown.png"
        private const val LEFT_DOWN_ARROW_HILITE  = "/images/WowheadTalentCalc/arrows/leftdown2.png"
        private const val RIGHT_ARROW             = "/images/WowheadTalentCalc/arrows/right.png"
        private const val RIGHT_ARROW_HILITE      = "/images/WowheadTalentCalc/arrows/right2.png"
        private const val RIGHT_DOWN_ARROW        = "/images/WowheadTalentCalc/arrows/rightdown.png"
        private const val RIGHT_DOWN_ARROW_HILITE = "/images/WowheadTalentCalc/arrows/rightdown2.png"
    }

    //private val talentButtons = observableListOf<LabeledTalentButton>()

    init {
        gridpane {
            style {
                backgroundImage += URI(spec.backgroundImage)
            }
            addClass(SpecStyles.talentTreeBackground)

            for (talent: Talent in spec.talents) {
                val viewModel = TalentButtonViewModel(wowClass, spec, talent)
                val scope = Scope(viewModel)
                val talentButton = find<TalentButtonFragment>(scope)
                this.add(talentButton.root, talent.location.column, talent.location.row)
            }
        }
        /*
        pane {
            isMouseTransparent = true
            for (depButton in talentButtons) {
                val dependency = depButton.talent
                val prereqLocation = dependency.prerequisite ?: continue
                val prereqButton = talentButtons.first { it.talent.location == prereqLocation }

                val depBounds = depButton.boundsInParentProperty()
                val prereqBounds = prereqButton.boundsInParentProperty()

                val rowDiff = dependency.location.row - prereqLocation.row
                val colDiff = dependency.location.column - prereqLocation.column

                if (rowDiff != 0) {
                    // Vertical arrows always go from top to bottom, so no need to worry about the reverse case.
                    rectangle {
                        fill = Color.MAGENTA
                        width = ARROW_THICKNESS
                        heightProperty().bind(prereqBounds.doubleBinding(depBounds) {
                            var newHeight = depBounds.get().minY - it!!.maxY + (BUTTON_INSETS + ARROW_ALIGN)
                            newHeight += if (colDiff != 0)
                                depBounds.get().height / 2.0
                            else
                                BUTTON_INSETS + ARROW_ALIGN
                            newHeight
                        })
                        xProperty().bind(prereqBounds.doubleBinding { (it!!.minX + it.maxX) / 2.0 - ARROW_THICKNESS / 2.0 })
                        yProperty().bind(prereqBounds.doubleBinding { it!!.maxY - (BUTTON_INSETS + ARROW_ALIGN) })
                    }
                }
                if (colDiff != 0) {
                    // horizontal
                    rectangle {
                        fill = Color.RED
                        widthProperty().bind(prereqBounds.doubleBinding(depBounds) {
                            val xDiff = if (colDiff > 0)
                                depBounds.get().minX - prereqBounds.get().maxX
                            else
                                prereqBounds.get().minX - depBounds.get().maxX

                            var newWidth = xDiff + (BUTTON_INSETS + ARROW_ALIGN)

                            newWidth += if (rowDiff != 0)
                                depBounds.get().width / 2.0
                            else
                                (BUTTON_INSETS + ARROW_ALIGN)

                            newWidth
                        })
                        height = ARROW_THICKNESS
                        xProperty().bind(depBounds.doubleBinding {
                            if (colDiff > 0) {
                                // left--->right
                                if (rowDiff != 0)
                                    it!!.minX - depBounds.get().width / 2.0
                                else
                                    it!!.minX - (BUTTON_INSETS + ARROW_ALIGN)
                            } else {
                                //right<---left
                                it!!.maxX - (BUTTON_INSETS + ARROW_ALIGN)
                            }
                        })

                        yProperty().bind(depBounds.doubleBinding { (it!!.minY + it.maxY) / 2.0 - ARROW_THICKNESS / 2.0 })
                    }
                }
            }
        }
         */
    }

    override fun computeMinWidth(height: Double): Double {
        return computePrefWidth(height)
    }

    override fun computeMinHeight(width: Double): Double {
        return computePrefHeight(width)
    }

    override fun computeMaxWidth(height: Double): Double {
        return computePrefWidth(height)
    }

    override fun computeMaxHeight(width: Double): Double {
        return computePrefHeight(width)
    }
}

fun EventTarget.talenttreeview(wowClass: WowClass, spec: Specialization,  op: TalentTreeView.() -> Unit = {}) =
    opcr(this, TalentTreeView(wowClass, spec).apply(op))
