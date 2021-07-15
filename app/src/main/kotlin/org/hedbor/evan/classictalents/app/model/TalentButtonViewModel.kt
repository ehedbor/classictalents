package org.hedbor.evan.classictalents.app.model

import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.hedbor.evan.classictalents.app.util.bindWhenNotNull
import org.hedbor.evan.classictalents.app.util.generateGrayscaleImage
import org.hedbor.evan.classictalents.app.util.selectInteger
import org.hedbor.evan.classictalents.app.view.TalentTooltip
import org.hedbor.evan.classictalents.common.model.Specialization
import org.hedbor.evan.classictalents.common.model.Talent
import org.hedbor.evan.classictalents.common.model.WowClass
import tornadofx.*


class TalentButtonViewModel(initialWowClass: WowClass, initialSpec: Specialization, initialTalent: Talent) : ViewModel() {
    val wowClassProperty = SimpleObjectProperty(initialWowClass)
    var wowClass: WowClass by wowClassProperty

    val specializationProperty = SimpleObjectProperty(initialSpec)
    var specialization: Specialization by specializationProperty

    val talentProperty = SimpleObjectProperty(initialTalent)
    var talent: Talent by talentProperty

    /**
     * The number of talent points that must be allocated in prerequisite rows before this talent is unlocked.
     */
    internal val requiredPoints = talent.locationProperty.selectInteger { it.rowProperty }.multiply(5)

    /**
     * The total number of talent points that have been allocated across all specs.
     */
    internal val totalAllocatedPoints = run {
        // note that this will NOT WORK if elements are added or removed!
        // this shouldn't be a problem because this application will not change the models (aside from the talent's rank)
        val allTalentRanks = wowClass.specializations.flatMap { it.talentsProperty }.map { it.rankProperty }.toTypedArray()
        Bindings.createIntegerBinding({ allTalentRanks.sumOf { it.value } }, *allTalentRanks)
    }

    /**
     * Whether or not the prerequisite talent rows for this talent have been filed out.
     */
    internal val isTalentRowUnlocked = totalAllocatedPoints ge requiredPoints

    /**
     * This talent's prerequisite, if present.
     */
    internal val prerequisite = objectBinding(specialization.talentsProperty, talent.prerequisiteProperty) {
        if (talent.prerequisite != null && !specialization.talents.isNullOrEmpty()) {
            specialization.talents.find { it.location == talent.prerequisite }
        } else {
            null
        }
    }

    /**
     * Whether or not this talent's prerequisite has been maxed out.
     * If the talent does not have a prerequisite, this is true.
     */
    internal val isPrerequisiteMaxedOut = SimpleBooleanProperty().bindWhenNotNull(prerequisite, true) { prerequisite ->
        booleanBinding(prerequisite.rankProperty, prerequisite.maxRankProperty) {
            prerequisite.rank >= prerequisite.maxRank
        }
    }

    /**
     * Whether or not the user still has unassigned talent points.
     */
    internal val hasUnassignedTalentPoints = totalAllocatedPoints.booleanBinding(wowClass.eraProperty) {
        val pointsAtMaxLevel = wowClass.era.getAvailablePoints(wowClass.era.maxLevel)
        (it as Int) < pointsAtMaxLevel
    }

    /**
     * Whether or not the user is able to remove talent points from this particular talent.
     * This is true if removing a point from this talent will not make any later talents to become non-allocatable.
     */
    val isDeallocatable = run {
        val dependencies: Array<Observable> = mutableListOf<Observable>().apply {
            this += specialization.talents.map { it.prerequisiteProperty}
            this += specialization.talents.map { it.rankProperty }
            this += specialization.talents.map { it.location.rowProperty }
        }.toTypedArray()

        Bindings.createBooleanBinding({
            // you cannot remove points if doing so would cause a later talent to not be allocatable
            // first, check this talent's dependency, if any
            val dependency = specialization.talents.firstOrNull { talent.location == it.prerequisite }
            if (dependency != null && dependency.rank > 0) {
                return@createBooleanBinding false
            }

            // next, find the row of the highest allocated talent.
            // if this talent is in the final row, a point can be removed
            // and no, past me, this doesn't this ignore the possibility that highestTalent might have a dependency
            // in the same row. we literally just checked if this talent has a dependency
            val highestTalent = specialization.talents
                .filter { it.rank > 0 }
                .maxByOrNull { it.location.row }
            if (highestTalent == null || talent.location.row == highestTalent.location.row) {
                return@createBooleanBinding true
            }

            // otherwise, determine the total number of allocated points in all tiers lower than the highest.
            // if removing a point would cause this total to be less than the requirement of the highest talent,
            // then a point cannot be removed
            val totalPoints = specialization.talents
                .filter { it.location.row < highestTalent.location.row }
                .sumOf { it.rank }

            val highestTalentRequirement = highestTalent.location.row * 5
            
            totalPoints - 1 >= highestTalentRequirement
        }, *dependencies)
    }

    /**
     * Whether or not talent points are able to be allocated into this talent. This is true if:
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
    val hasBeenAllocated = talent.rankProperty ge 1

    /**
     * True if [Talent.maxRank] points have been allocated into this talent.
     */
    val isMaxedOut = talent.rankProperty ge talent.maxRankProperty

    /**
     * Whether or not the user can actually add more points to this talent.
     */
    val canAcceptPoints = !isMaxedOut and (isAllocatable or hasBeenAllocated)

    private val normalBackgroundImage = talent.iconProperty.objectBinding { runCatching { Image(it) }.getOrNull() }
    private val grayscaleBackgroundImage = normalBackgroundImage.objectBinding { it?.let { generateGrayscaleImage(it) } }

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

    val rankCounterText = talent.rankProperty.asString()!!

    val tooltip = TalentTooltip(TalentTooltipViewModel(this))

    init {
        rebindOnChange(wowClassProperty)
        rebindOnChange(specializationProperty)
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