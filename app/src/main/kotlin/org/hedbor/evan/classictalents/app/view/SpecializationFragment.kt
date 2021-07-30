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

import javafx.geometry.Pos
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import org.hedbor.evan.classictalents.app.model.SpecializationViewModel
import org.hedbor.evan.classictalents.app.view.styles.SpecStyles
import org.hedbor.evan.classictalents.app.view.styles.TalentStyles
import org.hedbor.evan.classictalents.app.view.styles.TalentTooltipStyles
import org.hedbor.evan.classictalents.common.model.Location
import tornadofx.*


class SpecializationFragment : Fragment() {
    companion object {
        // TODO: move styling, etc elsewhere
        private const val BUTTON_INSETS = 15.0
        // The button textures have blank pixels along the edges. This alignment variable accounts for that
        private const val ARROW_ALIGN = 2.0
        private const val ARROW_THICKNESS = 5.0
    }

    private val model by inject<SpecializationViewModel>()
    private val talentButtons = observableMapOf<Location, Region>()

    override val root = borderpane {
        addClass(SpecStyles.specBorder)

        top = generateHeader()
        center = stackpane {
            addClass(SpecStyles.specBackground)
            generateTalentGrid()
            generateTalentArrowOverlay()
        }
    }

    private fun Region.generateHeader() = borderpane {
        addClass(SpecStyles.specHeader)
        center = hbox {
            alignment = Pos.BASELINE_CENTER
            spacing = 5.0
            stackpane {
                val imageSize = 27.0
                imageview(model.iconImage) {
                    addClass(TalentStyles.talentIcon)
                    fitWidth = imageSize
                    fitHeight = imageSize
                }
                imageview(model.borderImage) {
                    addClass(TalentStyles.talentIcon)
                    fitWidth = imageSize + 3
                    fitHeight = imageSize + 3
                }
            }

            label(model.specTitleText) {
                addClass(SpecStyles.specTitle)
            }
            label(model.pointCounterText) {
                addClass(SpecStyles.specTitle)
            }
        }
        right = hbox {
            alignment = Pos.BASELINE_CENTER
            button {
                addClass(SpecStyles.specResetButton)
                setOnMouseClicked(model::onResetButtonClicked)
                tooltip(model.resetButtonTooltip) {
                    addClass(TalentTooltipStyles.tooltipError)
                }
            }
        }
    }

    private fun Region.generateTalentGrid() = gridpane {
        addClass(SpecStyles.specBackground)
        backgroundProperty().bind(model.backgroundImage)
        // add rounded corners to the background image
        // (-fx-background-radius doesnt work on images)
        shape = Rectangle().apply {
            arcWidth = 20.0
            arcHeight = 20.0
            widthProperty().bind(this@gridpane.widthProperty())
            heightProperty().bind(this@gridpane.heightProperty())
        }

        for (talent in model.talents) {
            val scope = Scope(model.getViewModel(talent))
            val talentButton = find<TalentButtonFragment>(scope)
            talentButtons += talent.location to talentButton.root
            this.add(talentButton.root, talent.location.column, talent.location.row)
        }
    }

    private fun Region.generateTalentArrowOverlay() = pane {
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