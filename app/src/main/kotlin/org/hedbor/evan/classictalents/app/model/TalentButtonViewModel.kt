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

import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.hedbor.evan.classictalents.app.service.ImageService
import org.hedbor.evan.classictalents.app.util.bindWhenNotNull
import org.hedbor.evan.classictalents.app.util.selectInteger
import org.hedbor.evan.classictalents.app.view.TalentTooltip
import org.hedbor.evan.classictalents.common.model.Talent
import tornadofx.*


class TalentButtonViewModel(private val specializationViewModel: SpecializationViewModel, initialTalent: Talent) : ViewModel() {
    private val talentProperty = SimpleObjectProperty(initialTalent)
    private var talent: Talent by talentProperty

    val classKey = specializationViewModel.classKey
    val specKey = specializationViewModel.specKey
    val talentKey = bind { talent.translationKeyProperty }.stringBinding(specKey) { "${specKey.value}.$it" }

    val rank = bind { talent.rankProperty }
    val maxRank = bind { talent.maxRankProperty }

    val spell = bind { talent.spellProperty }

    /**
     * The number of talent points that must be allocated in prerequisite rows before this talent is unlocked.
     */
    val requiredPoints = bind { talent.locationProperty }.selectInteger { it.rowProperty }.multiply(5)!!

    /**
     * The total number of talent points that have been allocated across all specs.
     */
    private val totalPointsForClass = specializationViewModel.totalPointsForClass

    /**
     * The total number of talent points that have been allocated for this spec.
     */
    private val totalPointsForSpec = specializationViewModel.totalPointsForSpec

    /**
     * Whether the prerequisite talent rows for this talent have been filed out.
     */
    val isTalentRowUnlocked = totalPointsForSpec ge requiredPoints

    /**
     * This talent's prerequisite, if present.
     */
    val prerequisite = objectBinding(specializationViewModel.talents, talent.prerequisiteProperty) {
        if (talent.prerequisite != null && !specializationViewModel.talents.isEmpty()) {
            specializationViewModel.talents.find { it.location == talent.prerequisite }
        } else {
            null
        }
    }

    /**
     * Whether this talent's prerequisite has been maxed out.
     * If the talent does not have a prerequisite, this is true.
     */
    val isPrerequisiteMaxedOut = SimpleBooleanProperty().bindWhenNotNull(prerequisite, true) { prerequisite ->
        booleanBinding(prerequisite.rankProperty, prerequisite.maxRankProperty) {
            prerequisite.rank >= prerequisite.maxRank
        }
    }

    /**
     * Whether the user still has unassigned talent points.
     */
    private val hasUnassignedTalentPoints = totalPointsForClass.booleanBinding(specializationViewModel.era) { totalPoints ->
        val era = specializationViewModel.era.value
        val pointsAtMaxLevel = era.getAvailablePoints(era.maxLevel)
        (totalPoints as Int) < pointsAtMaxLevel
    }

    /**
     * Whether the user is able to remove talent points from this particular talent.
     * This is true if removing a point from this talent will not make any later talents to become non-allocatable.
     */
    val isDeallocatable = run {
        val talents = specializationViewModel.talents
        val dependencies: Array<Observable> = mutableListOf<Observable>().apply {
            this += talents.map { it.prerequisiteProperty}
            this += talents.map { it.rankProperty }
            this += talents.map { it.location.rowProperty }
        }.toTypedArray()

        Bindings.createBooleanBinding({
            // you cannot remove points if doing so would cause a later talent to not be allocatable
            // first, check this talent's dependency, if any
            val dependency = talents.firstOrNull { talent.location == it.prerequisite }
            if (dependency != null && dependency.rank > 0) {
                return@createBooleanBinding false
            }

            // next, find the row of the highest allocated talent.
            // if this talent is in the final row, a point can be removed
            // and no, past me, this doesn't this ignore the possibility that highestTalent might have a dependency
            // in the same row. we literally just checked if this talent has a dependency
            val highestTalent = talents.filter { it.rank > 0 }.maxByOrNull { it.location.row }
            if (highestTalent == null || talent.location.row == highestTalent.location.row) {
                return@createBooleanBinding true
            }

            // otherwise, determine the total number of allocated points in all tiers lower than the highest.
            // if removing a point would cause this total to be less than the requirement of the highest talent,
            // then a point cannot be removed
            val totalPoints = talents.filter { it.location.row < highestTalent.location.row }.sumOf { it.rank }
            val highestTalentRequirement = highestTalent.location.row * 5
            
            totalPoints - 1 >= highestTalentRequirement
        }, *dependencies)!!
    }

    /**
     * Whether talent points can be allocated into this talent. This is true if:
     * A) This talent's row is unlocked,
     * B) This talent's [prerequisite][Talent.prerequisite] (if any) is maxed out, and
     * C) The user still has unassigned talent points.
     *
     * Note that the value of this property is independent of how many points have actually
     * been allocated into this talent.
     */
    val isAllocatable = isTalentRowUnlocked and isPrerequisiteMaxedOut and hasUnassignedTalentPoints

    /**
     * True if at least one talent point has been allocated into this talent.
     */
    val hasBeenAllocated = rank ge 1

    /**
     * True if [Talent.maxRank] points have been allocated into this talent.
     */
    val isMaxedOut = rank ge maxRank

    /**
     * Whether the user can actually add more points to this talent.
     */
    val canAcceptPoints = !isMaxedOut and (isAllocatable or hasBeenAllocated)

    private val normalBackgroundImage = bind { talent.iconProperty }.objectBinding { it?.runCatching { Image(this) }?.getOrNull() }
    private val grayscaleBackgroundImage = normalBackgroundImage.objectBinding { it?.let { ImageService.toGrayscale(it) } }

    val backgroundImage = objectBinding(isAllocatable, hasBeenAllocated, normalBackgroundImage, grayscaleBackgroundImage) {
        if (isAllocatable.value || hasBeenAllocated.value) {
            normalBackgroundImage.value
        } else {
            grayscaleBackgroundImage.value
        }
    }

    val borderImage = Image("/images/Icon/large/border/default.png")
    val borderHiliteImage = Image("/images/Icon/large/hilite/hilite.png")

    val buttonWidth = borderImage.width
    val buttonHeight = borderImage.height

    val rankCounterText = rank.asString()!!

    val tooltip = TalentTooltip(TalentTooltipViewModel(this))

    init {
        rebindOnChange(talentProperty)
    }

    fun onMouseClicked(event: MouseEvent) {
        when (event.button) {
            MouseButton.PRIMARY -> {
                if (isAllocatable.value && !isMaxedOut.value) {
                    talent.rank += 1
                }
            }
            MouseButton.SECONDARY -> {
                if (isDeallocatable.value && hasBeenAllocated.value) {
                    talent.rank -= 1
                }
            }
            else -> {}
        }
    }
}