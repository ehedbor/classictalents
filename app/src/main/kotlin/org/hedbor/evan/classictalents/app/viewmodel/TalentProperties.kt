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

import javafx.beans.property.SimpleBooleanProperty
import org.hedbor.evan.classictalents.app.model.Specialization
import org.hedbor.evan.classictalents.app.model.Talent
import org.hedbor.evan.classictalents.app.model.WowClass
import org.hedbor.evan.classictalents.app.service.ImageService
import org.hedbor.evan.classictalents.app.util.bindWhenNotNull
import org.hedbor.evan.classictalents.app.viewmodel.TalentButtonViewModel.Companion.INNER_ICON_SIZE
import tornadofx.*


/**
 * Container to separate ui-facing properties from internal calculated properties.
 */
class TalentProperties(val wowClass: WowClass, val spec: Specialization, val talent: Talent) {
    val specKey = "${wowClass.translationKey}.${spec.translationKey}"
    val fullKey = "$specKey.${talent.translationKey}"

    val normalBackgroundImage = ImageService.loadImage(talent.icon, INNER_ICON_SIZE, INNER_ICON_SIZE)
    val grayscaleBackgroundImage = ImageService.toGrayscale(normalBackgroundImage)

    /**
     * The number of talent points that must be allocated in prerequisite rows before this talent is unlocked.
     */
    val requiredPoints get() = talent.location.row * 5

    /**
     * Whether the prerequisite talent rows for this talent have been filled out.
     */
    val isTalentRowUnlocked = spec.totalPointsProperty ge requiredPoints

    /**
     * This talent's prerequisite, if present.
     */
    val prerequisite = objectBinding(spec.talentsProperty) {
        if (talent.prerequisite != null && spec.talents.isNotEmpty()) {
            spec.talents.find { it.location == talent.prerequisite }
        } else {
            null
        }
    }

    /**
     * Whether this talent's prerequisite has been maxed out.
     * If the talent does not have a prerequisite, this is true.
     */
    val isPrerequisiteMaxedOut = SimpleBooleanProperty().bindWhenNotNull(prerequisite, true) { prerequisite ->
        prerequisite.rankProperty.booleanBinding {
            prerequisite.rank >= prerequisite.maxRank
        }
    }

    /**
     * Whether the user still has unassigned talent points.
     */
    val hasUnassignedTalentPoints by lazy {
        val pointsAtMaxLevel = wowClass.era.getAvailablePoints(wowClass.era.maxLevel)
        wowClass.totalPoints < pointsAtMaxLevel
    }

    /**
     * Whether the user is able to remove talent points from this particular talent.
     * This is true if removing a point from this talent will not make any later talents to become non-allocatable.
     */
    val isDeallocatable = spec.talentsProperty.booleanBinding bind@{ talents ->
        // you cannot remove points if doing so would cause a later talent to not be allocatable
        // first, check this talent's dependency, if any
        val dependency = talents!!.firstOrNull { talent.location == it.prerequisite }
        if (dependency != null && dependency.rank > 0) {
            return@bind false
        }

        // next, find the row of the highest allocated talent.
        // if this talent is in the final row, a point can be removed
        // and no, past me, this doesn't this ignore the possibility that highestTalent might have a dependency
        // in the same row. we literally just checked if this talent has a dependency
        val highestTalent = talents.filter { it.rank > 0 }.maxByOrNull { it.location.row }
        if (highestTalent == null || talent.location.row == highestTalent.location.row) {
            return@bind true
        }

        // otherwise, determine the total number of allocated points in all tiers lower than the highest.
        // if removing a point would cause this total to be less than the requirement of the highest talent,
        // then a point cannot be removed
        val totalPoints = talents.filter { it.location.row < highestTalent.location.row }.sumOf { it.rank }
        val highestTalentRequirement = highestTalent.location.row * 5

        totalPoints - 1 >= highestTalentRequirement
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
    val isAllocatable = isPrerequisiteMaxedOut and isTalentRowUnlocked and hasUnassignedTalentPoints

    /**
     * True if at least one talent point has been allocated into this talent.
     */
    val hasBeenAllocated = talent.rankProperty ge 1

    /**
     * True if [Talent.maxRank] points have been allocated into this talent.
     */
    val isMaxedOut = talent.rankProperty ge talent.maxRank

    /**
     * Whether the user can actually add more points to this talent.
     */
    val canAcceptPoints = !isMaxedOut and (isAllocatable or hasBeenAllocated)
}