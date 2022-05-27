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

package org.hedbor.evan.classictalents.app.viewmodel

import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Bounds
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import org.hedbor.evan.classictalents.app.model.Location
import org.hedbor.evan.classictalents.app.model.Specialization
import org.hedbor.evan.classictalents.app.model.Talent
import org.hedbor.evan.classictalents.app.model.WowClass
import org.hedbor.evan.classictalents.app.service.ImageService
import org.hedbor.evan.classictalents.app.view.styles.StyleConstants
import tornadofx.*


class SpecializationViewModel(val wowClass: WowClass, val specialization: Specialization) : ViewModel() {
    private val specKey = "${wowClass.translationKey}.${specialization.translationKey}"

    val talents get() = specialization.talents

    val specTitleText = messages[specKey]!!
    val pointCounterText = specialization.totalPointsProperty.stringBinding { "($it)" }
    val resetButtonTooltip = messages["spec.reset"]!!

    val backgroundImage = SimpleObjectProperty<Background>().apply {
        val image = ImageService.loadImage(specialization.backgroundImage)

        // TODO: Find a way to specify this part via CSS while still allowing the background image to be dynamically modified
        val repeat = BackgroundRepeat.NO_REPEAT
        val pos = BackgroundPosition.CENTER
        val size = BackgroundSize(1.0, 1.0, true, true, true, true)
        val backgroundImage = BackgroundImage(image, repeat, repeat, pos, size)
        val backgroundFill = BackgroundFill(StyleConstants.LIGHT_BACKGROUND_COLOR, null, null)

        value = Background(listOf(backgroundFill), listOf(backgroundImage))
    }

    val iconImage = ImageService.loadImage(specialization.icon)

    val borderImage = ImageService.loadImage("/images/Icon/large/border/default.png")

    fun getPrerequisiteFor(dependencyLocation: Location): Talent? {
        val dep = specialization.talents.firstOrNull { it.location == dependencyLocation } ?: return null
        return specialization.talents.firstOrNull { it.location == dep.prerequisite }
    }

    fun onResetButtonClicked(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            specialization.talents.forEach { talent ->
                talent.rank = 0
            }
        }
    }

    fun getArrowContext(
        prerequisite: Talent,
        prereqBounds: ReadOnlyObjectProperty<Bounds>,
        dependBounds: ReadOnlyObjectProperty<Bounds>,
        rowDiff: Int,
        colDiff: Int
    ) = ArrowContext(prerequisite, prereqBounds, dependBounds, rowDiff, colDiff)

    inner class ArrowContext(
        prereq: Talent,
        prereqBounds: ReadOnlyObjectProperty<Bounds>,
        dependBounds: ReadOnlyObjectProperty<Bounds>,
        rowDiff: Int,
        colDiff: Int
    ) {
        val isVertical = rowDiff != 0
        val isTopToBottom = rowDiff > 0
        val isHorizontal = colDiff != 0
        val isLeftToRight = colDiff > 0

        val prereqBoundsProperty: ReadOnlyObjectProperty<Bounds> = SimpleObjectProperty<Bounds>().apply { bind(prereqBounds) }
        val prereqBounds: Bounds by prereqBoundsProperty

        val dependBoundsProperty: ReadOnlyObjectProperty<Bounds> = SimpleObjectProperty<Bounds>().apply { bind(dependBounds) }
        val dependBounds: Bounds by dependBoundsProperty

        val verticalImageProperty: ReadOnlyObjectProperty<Image> = SimpleObjectProperty<Image>().apply { bind(getVerticalImageFor(prereq)) }
        val verticalImage: Image by verticalImageProperty

        val horizontalImageProperty: ReadOnlyObjectProperty<Image> = SimpleObjectProperty<Image>().apply { bind(getHorizontalImageFor(prereq, isVertical, isLeftToRight)) }
        val horizontalImage: Image by horizontalImageProperty

        @Suppress("UNCHECKED_CAST")
        private fun getVerticalImageFor(prerequisite: Talent): ObjectBinding<Image> {
            return prerequisite.rankProperty.objectBinding {
                if (prerequisite.rank < prerequisite.maxRank) {
                    Image("images/WowheadTalentCalc/arrows/down.png")
                } else {
                    Image("images/WowheadTalentCalc/arrows/down2.png")
                }
            } as ObjectBinding<Image>
        }

        @Suppress("UNCHECKED_CAST")
        private fun getHorizontalImageFor(prerequisite: Talent, isVertical: Boolean, isLeftToRight: Boolean): ObjectBinding<Image> {
            return prerequisite.rankProperty.objectBinding {
                val horizontalComponent = if (isLeftToRight) "right" else "left"
                val verticalComponent = if (isVertical) "down" else ""
                val maxRankComponent = if (prerequisite.rank >= prerequisite.maxRank) "2" else ""

                val imageName = "$horizontalComponent$verticalComponent$maxRankComponent.png"
                Image("images/WowheadTalentCalc/arrows/$imageName")
            } as ObjectBinding<Image>
        }
    }
}