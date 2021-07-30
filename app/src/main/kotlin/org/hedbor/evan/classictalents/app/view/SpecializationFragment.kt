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

package org.hedbor.evan.classictalents.app.view

import javafx.scene.layout.Region
import javafx.scene.paint.Color
import org.hedbor.evan.classictalents.app.model.SpecializationViewModel
import org.hedbor.evan.classictalents.common.model.Location
import tornadofx.*


class SpecializationFragment : Fragment() {
    companion object {
        // TODO: move styling, etc elsewhere
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

    private val model by inject<SpecializationViewModel>()
    private val talentButtons = observableMapOf<Location, Region>()

    override val root = stackpane {
        gridpane {
            backgroundProperty().bind(model.backgroundImage)

            for (talent in model.talents) {
                val scope = Scope(model.getViewModel(talent))
                val talentButton = find<TalentButtonFragment>(scope)
                talentButtons += talent.location to talentButton.root
                this.add(talentButton.root, talent.location.column, talent.location.row)
            }
        }
        pane {
            isMouseTransparent = true
            for ((dependencyLocation, dependencyButton) in talentButtons) {
                val prerequisiteLocation = model.getPrerequisiteLocationFor(dependencyLocation) ?: continue
                val prerequisiteButton = talentButtons.firstNotNullOf { (loc, but) -> if (loc == prerequisiteLocation) but else null }

                val depBounds = dependencyButton.boundsInParentProperty()
                val prereqBounds = prerequisiteButton.boundsInParentProperty()

                val rowDiff = dependencyLocation.row - prerequisiteLocation.row
                val colDiff = dependencyLocation.column - prerequisiteLocation.column

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
    }
}