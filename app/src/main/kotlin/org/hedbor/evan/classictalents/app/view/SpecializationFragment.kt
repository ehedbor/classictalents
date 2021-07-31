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
import javafx.geometry.Rectangle2D
import javafx.scene.layout.Region
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
        // internal insets of the button + 2 extra pixels to make room for the transparent space around the button
        private const val BUTTON_INSETS = 15.0 + 2.0
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

            val isVertical = rowDiff != 0
            val isTopToBottom = rowDiff > 0
            val isHorizontal = colDiff != 0
            val isLeftToRight = colDiff > 0

            if (isVertical) {
                // Vertical arrows always go from top to bottom, so no need to worry about the reverse case.
                check(isTopToBottom) { "Attempted to draw vertical arrow from bottom to top -- this is not allowed." }

                imageview("images/WowheadTalentCalc/arrows/down.png") {
                    viewportProperty().bind(objectBinding(prereqBounds, depBounds, image.widthProperty(), image.heightProperty()) {
                        val width = image.width
                        val yDiff = depBounds.value.minY - prereqBounds.value.maxY
                        val height = yDiff + if (isHorizontal)
                            depBounds.value.height / 2.0 + BUTTON_INSETS
                        else
                            BUTTON_INSETS * 2

                        val minX = 0.0
                        val minY = image.height - height

                        if (width >= 0 && height >= 0)
                            Rectangle2D(minX, minY, width, height)
                        else null
                    })
                    xProperty().bind(prereqBounds.doubleBinding(image.widthProperty()) { (it!!.minX + it.maxX - image.width) / 2.0 })
                    yProperty().bind(prereqBounds.doubleBinding { it!!.maxY - BUTTON_INSETS })
                }
            }
            if (isHorizontal) {
                val imageLocation = if (isLeftToRight) "images/WowheadTalentCalc/arrows/right.png" else "images/WowheadTalentCalc/arrows/left.png"
                imageview(imageLocation) {
                    viewportProperty().bind(objectBinding(prereqBounds, depBounds, image.widthProperty(), image.heightProperty()) {
                        val height = image.height

                        val xDiff = if (isLeftToRight)
                            depBounds.get().minX - prereqBounds.get().maxX
                        else
                            prereqBounds.get().minX - depBounds.get().maxX

                        val width = xDiff + if (isVertical)
                            depBounds.get().width / 2.0 + BUTTON_INSETS
                        else
                            BUTTON_INSETS + BUTTON_INSETS

                        val minY = 0.0
                        val minX = if (isLeftToRight) image.width - width else 0.0

                        if (width >= 0 && height >= 0)
                            Rectangle2D(minX, minY, width, height)
                        else null
                    })
                    xProperty().bind(depBounds.doubleBinding {
                        if (isLeftToRight) {
                            if (isVertical)
                                it!!.minX - depBounds.get().width / 2.0
                            else
                                it!!.minX - BUTTON_INSETS
                        } else {
                            it!!.maxX - BUTTON_INSETS
                        }
                    })

                    yProperty().bind(depBounds.doubleBinding(image.heightProperty()) { (it!!.minY + it.maxY - image.height) / 2.0 })
                }
            }
        }
    }
}