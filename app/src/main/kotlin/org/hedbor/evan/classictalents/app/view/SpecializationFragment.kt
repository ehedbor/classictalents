/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2022 Evan Hedbor
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
import org.hedbor.evan.classictalents.app.model.Location
import org.hedbor.evan.classictalents.app.view.styles.SpecStyles
import org.hedbor.evan.classictalents.app.view.styles.TalentStyles
import org.hedbor.evan.classictalents.app.view.styles.TalentTooltipStyles
import org.hedbor.evan.classictalents.app.viewmodel.SpecializationViewModel
import org.hedbor.evan.classictalents.app.viewmodel.TalentButtonViewModel
import tornadofx.*


class SpecializationFragment : Fragment() {
    companion object {
        private const val BUTTON_INSETS = 15.0
        private const val BUTTON_ALIGNMENT = (TalentButtonViewModel.BORDER_SIZE - TalentButtonViewModel.INNER_ICON_SIZE) / 4.0
        private const val ADJUSTED_BUTTON_INSETS = BUTTON_INSETS + BUTTON_ALIGNMENT
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
            alignment = Pos.CENTER
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
            alignment = Pos.CENTER
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
            val scope = Scope(TalentButtonViewModel(model.wowClass, model.specialization, talent))
            val fragment = find<TalentButtonFragment>(scope)
            val root = fragment.root

            talentButtons += talent.location to root
            this.add(fragment.root, talent.location.column, talent.location.row)
        }
    }

    private fun Region.generateTalentArrowOverlay() = pane {
        isMouseTransparent = true
        for ((dependencyLocation, dependencyButton) in talentButtons) {
            val prerequisite = model.getPrerequisiteFor(dependencyLocation) ?: continue
            val prerequisiteButton = talentButtons.firstNotNullOf { (loc, but) -> if (loc == prerequisite.location) but else null }

            val prereqBounds = prerequisiteButton.boundsInParentProperty()
            val dependBounds = dependencyButton.boundsInParentProperty()

            val rowDiff = dependencyLocation.row - prerequisite.location.row
            val colDiff = dependencyLocation.column - prerequisite.location.column

            val context = model.getArrowContext(prerequisite, prereqBounds, dependBounds, rowDiff, colDiff)

            if (context.isVertical) generateVerticalArrow(context)
            if (context.isHorizontal) generateHorizontalArrow(context)
        }
    }

    private fun Region.generateVerticalArrow(ctx: SpecializationViewModel.ArrowContext) = imageview {
        // Vertical arrows always go from top to bottom, so no need to worry about the reverse case.
        check(ctx.isTopToBottom) { "Attempted to draw vertical arrow from bottom to top -- this is not allowed." }

        imageProperty().bind(ctx.verticalImageProperty)
        viewportProperty().bind(objectBinding(ctx.prereqBoundsProperty, ctx.dependBoundsProperty, imageProperty(), ctx.horizontalImageProperty) {
            val width = image.width
            val yDiff = ctx.dependBounds.minY - ctx.prereqBounds.maxY
            val height = yDiff + if (ctx.isHorizontal)
                (ctx.dependBounds.height - ctx.horizontalImage.height) / 2.0 + ADJUSTED_BUTTON_INSETS
            else
                ADJUSTED_BUTTON_INSETS * 2

            val minX = 0.0
            val minY = image.height - height

            if (width >= 0 && height >= 0)
                Rectangle2D(minX, minY, width, height)
            else null
        })
        xProperty().bind(doubleBinding(ctx.prereqBoundsProperty, ctx.dependBoundsProperty, imageProperty()) {
            if (ctx.isHorizontal && !ctx.isLeftToRight) {
                ctx.dependBounds.maxX - (ctx.prereqBounds.width + image.width) / 2.0
            } else {
                ctx.dependBounds.minX + (ctx.dependBounds.width - image.width) / 2.0
            }
        })
        yProperty().bind(doubleBinding(ctx.prereqBoundsProperty) {
            if (ctx.isHorizontal) {
                ctx.prereqBounds.maxY - (ctx.prereqBounds.height - image.width) / 2.0
            } else {
                ctx.prereqBounds.maxY - ADJUSTED_BUTTON_INSETS
            }
        })
    }

    private fun Region.generateHorizontalArrow(ctx: SpecializationViewModel.ArrowContext) = imageview {
        imageProperty().bind(ctx.horizontalImageProperty)
        viewportProperty().bind(objectBinding(ctx.prereqBoundsProperty, ctx.dependBoundsProperty, imageProperty(), ctx.verticalImageProperty) {
            val height = image.height

            val xDiff = if (ctx.isLeftToRight)
                ctx.dependBounds.minX - ctx.prereqBounds.maxX
            else
                ctx.prereqBounds.minX - ctx.dependBounds.maxX

            val width = xDiff + if (ctx.isVertical)
                (ctx.dependBounds.width + ctx.verticalImage.width) / 2.0  + ADJUSTED_BUTTON_INSETS
            else
                ADJUSTED_BUTTON_INSETS + ADJUSTED_BUTTON_INSETS

            val minY = 0.0
            val minX = if (ctx.isLeftToRight) image.width - width else 0.0

            if (width >= 0 && height >= 0)
                Rectangle2D(minX, minY, width, height)
            else null
        })
        xProperty().bind(doubleBinding(ctx.prereqBoundsProperty, ctx.dependBoundsProperty, ctx.verticalImageProperty) {
            if (ctx.isVertical) {
                if (ctx.isLeftToRight) {
                    ctx.prereqBounds.maxX - ADJUSTED_BUTTON_INSETS
                } else {
                    ctx.dependBounds.maxX - (ctx.prereqBounds.width + ctx.verticalImage.width) / 2.0
                }
            } else {
                if (ctx.isLeftToRight) {
                    ctx.prereqBounds.maxX - ADJUSTED_BUTTON_INSETS
                } else {
                    ctx.dependBounds.maxX - ADJUSTED_BUTTON_INSETS
                }
            }
        })
        yProperty().bind(doubleBinding(ctx.dependBoundsProperty, ctx.prereqBoundsProperty, imageProperty()) {
            (ctx.prereqBounds.minY + ctx.prereqBounds.maxY - image.height) / 2.0
        })
    }
}