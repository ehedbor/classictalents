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

package org.hedbor.evan.classictalents.app.model

import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Bounds
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import org.hedbor.evan.classictalents.app.view.styles.StyleConstants
import org.hedbor.evan.classictalents.common.model.Location
import org.hedbor.evan.classictalents.common.model.Specialization
import org.hedbor.evan.classictalents.common.model.Talent
import tornadofx.*


class SpecializationViewModel(private val wowClassViewModel: WowClassViewModel, initialSpec: Specialization) : ViewModel() {
    private val specializationProperty = SimpleObjectProperty(initialSpec)
    private var specialization by specializationProperty

    val era  = bind { wowClassViewModel.era }

    val specializations = bind { wowClassViewModel.specializations }
    val talents = bind { specialization.talentsProperty }

    val totalPointsForClass = wowClassViewModel.totalAllocatedPoints
    val totalPointsForSpec = run {
        val allTalentRanks = specialization.talentsProperty.map { it.rankProperty }.toTypedArray()
        Bindings.createIntegerBinding({ allTalentRanks.sumOf { it.value } }, *allTalentRanks)!!
    }

    val classKey = bind { wowClassViewModel.translationKey }
    val specKey = bind { specialization.translationKeyProperty }.stringBinding(classKey) { "${classKey.value}.$it" }

    val specTitleText = specKey.stringBinding { messages[it!!] }
    val pointCounterText = totalPointsForSpec.stringBinding { "($it)" }
    val resetButtonTooltip = messages["spec.reset"]!!

    val backgroundImage = bind { specialization.backgroundImageProperty }.objectBinding { path ->
        val image = path?.runCatching { Image(path) }?.getOrNull() ?: return@objectBinding null

        // TODO: Find a way to specify this part via CSS while still allowing the background image to be dynamically modified
        val repeat = BackgroundRepeat.NO_REPEAT
        val pos = BackgroundPosition.CENTER
        val size = BackgroundSize(1.0, 1.0, true, true, true, true)
        val backgroundImage = BackgroundImage(image, repeat, repeat, pos, size)
        val backgroundFill = BackgroundFill(StyleConstants.LIGHT_BACKGROUND_COLOR, null, null)
        Background(listOf(backgroundFill), listOf(backgroundImage))
    }

    val iconImage = bind { specialization.iconProperty }.objectBinding { it?.runCatching { Image(this) }?.getOrNull() }

    val borderImage = SimpleObjectProperty(Image("/images/Icon/large/border/default.png"))

    init {
        rebindOnChange(specializationProperty)
    }

    fun getTalentViewModel(talent: Talent) = TalentButtonViewModel(this, talent)

    fun getPrerequisiteFor(dependencyLocation: Location): Talent? {
        val dep = talents.firstOrNull { it.location == dependencyLocation } ?: return null
        return talents.firstOrNull { it.location == dep.prerequisite }
    }

    fun onResetButtonClicked(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            talents.forEach {
                it.rank = 0
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
            return objectBinding(prerequisite.rankProperty, prerequisite.maxRankProperty) {
                if (prerequisite.rank < prerequisite.maxRank) {
                    Image("images/WowheadTalentCalc/arrows/down.png")
                } else {
                    Image("images/WowheadTalentCalc/arrows/down2.png")
                }
            } as ObjectBinding<Image>
        }

        @Suppress("UNCHECKED_CAST")
        private fun getHorizontalImageFor(prerequisite: Talent, isVertical: Boolean, isLeftToRight: Boolean): ObjectBinding<Image> {
            return objectBinding(prerequisite.rankProperty, prerequisite.maxRankProperty) {
                val horizontalComponent = if (isLeftToRight) "right" else "left"
                val verticalComponent = if (isVertical) "down" else ""
                val maxRankComponent = if (prerequisite.rank >= prerequisite.maxRank) "2" else ""

                val imageName = "$horizontalComponent$verticalComponent$maxRankComponent.png"
                Image("images/WowheadTalentCalc/arrows/$imageName")
            } as ObjectBinding<Image>
        }
    }
}