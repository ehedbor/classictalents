package org.hedbor.evan.classictalents.model

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.ReadOnlyIntegerProperty
import javafx.beans.property.ReadOnlyIntegerWrapper
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.scene.image.Image
import org.hedbor.evan.classictalents.util.*


@Suppress("MemberVisibilityCanBePrivate", "unused")
class Talent {
    private val _specialization = SimpleObjectProperty<Specialization>()
    var specialization: Specialization by _specialization.delegate()
    fun specializationProperty() = _specialization

    private val _name = SimpleStringProperty("")
    var name: String by _name.delegate()
    fun nameProperty() = _name

    private val _row = SimpleIntegerProperty()
    var row by _row.delegate()
    fun rowProperty() = _row

    private val _column = SimpleIntegerProperty()
    var column by _column.delegate()
    fun columnProperty() = _column

    private val _prerequisite = SimpleObjectProperty<Talent?>()
    var prerequisite by _prerequisite.delegate()
    fun prerequisiteProperty() = _prerequisite

    private val _maxRank = SimpleIntegerProperty(5)
    var maxRank by _maxRank.delegate()
    fun maxRankProperty() = _maxRank

    private val _rank = SimpleIntegerProperty(0)
    var rank by _rank.delegate()
    fun rankProperty() = _rank

    // TODO: default image
    private val _icon = SimpleObjectProperty<Image>()
    var icon: Image by _icon.delegate()
    fun iconProperty() = _icon

    private val _description = SimpleStringProperty("")
    var description: String by _description.delegate()
    fun descriptionProperty() = _description

    private val _spell = SimpleObjectProperty<Spell?>()
    var spell by _spell.delegate()
    fun spellProperty() = _spell

    /*
     * Computed properties:
     */

    private val _requiredPointsBinding = rowProperty().multiply(5)
    private val _requiredPoints = ReadOnlyIntegerWrapper().also { it.bind(_requiredPointsBinding) }
    val requiredPoints by _requiredPoints.delegate()
    fun requiredPointsProperty() = _requiredPoints.readOnlyProperty!!

    private val _maxedOutBinding = rankProperty().isEqualTo(maxRankProperty())
    private val _isMaxedOut = ReadOnlyBooleanWrapper().also { it.bind(_maxedOutBinding) }
    val isMaxedOut by _isMaxedOut.delegate()
    fun maxedOutProperty() = _isMaxedOut.readOnlyProperty!!

    private val _isRowUnlockedBinding =
        Bindings.`when`(specializationProperty().isNull).then(false)
            .otherwise(Bindings.selectInteger(specializationProperty(), "allocatedPoints")
                .greaterThanOrEqualTo(requiredPointsProperty()))
    private val _isRowUnlocked = ReadOnlyBooleanWrapper().also { it.bind(_isRowUnlockedBinding) }
    val isRowUnlocked by _isRowUnlocked.delegate()
    fun rowUnlockedProperty() = _isRowUnlocked.readOnlyProperty!!

    private val _canAllocBinding = run {
        val prereqMaxed =
            Bindings.`when`(prerequisiteProperty().isNull)
                .then(true)
                .otherwise(Bindings.selectBoolean(prerequisiteProperty(), "maxedOut"))

        val hasUnassignedPoints = Bindings.`when`(specializationProperty().isNull)
            .then(true)
            .otherwise(
                Bindings.`when`(Bindings.select<WowClass>(specializationProperty(), "wowClass").isNull)
                    .then(true)
                    .otherwise(Bindings.selectBoolean(specializationProperty(), "wowClass", "hasUnassignedPoints")))

        prereqMaxed.and(rowUnlockedProperty()).and(hasUnassignedPoints)
    }
    private val _canAllocate = SimpleBooleanProperty().also { it.bind(_canAllocBinding) }

    /** @see canAllocateProperty */
    val canAllocate by _canAllocate.delegate()

    /**
     * Whether talent points can be allocated into this talent. This is true if:
     * 1. This talent's [row is unlocked][rowUnlockedProperty],
     * 2. This talent's [prerequisiteProperty] (if any) is maxed out, and
     * 3. The user still has [unassigned talent points][WowClass.hasUnassignedPointsProperty].
     *
     * Note that the value of this property is independent of how many points have actually
     * been allocated into this talent.
     */
    fun canAllocateProperty() = _canAllocate as ReadOnlyBooleanProperty

    private val _canDeallocateBinding = Bindings.`when`(specializationProperty().isNull)
    private val _canDeallocate = ReadOnlyBooleanWrapper().also { prop ->
        // TODO: is this all we need to bind?
        specializationProperty().addListener { _, old, new ->
            if (old != null) prop.unbind()
            if (new != null) {
                prop.bind(new.talentsProperty().booleanBinding { talents ->
                    // you cannot remove points if doing so would cause a later talent to not be allocatable
                    // first, check this talent's dependency, if any
                    val dependency = talents.firstOrNull { this@Talent === it.prerequisite }
                    if (dependency != null && dependency.rank > 0) {
                        return@booleanBinding false
                    }

                    // next, find the row of the highest allocated talent.
                    // if this talent is in the final row, a point can be removed
                    val highestTalent = talents.filter { it.rank > 0 }.maxByOrNull { it.row }
                    if (highestTalent == null || this@Talent.row == highestTalent.row) {
                        return@booleanBinding true
                    }

                    // otherwise, determine the total number of allocated points in all tiers lower than
                    // the highest. if removing a point would cause this total to be less than the
                    // requirement of the highest talent, then a point cannot be removed
                    val totalPoints = talents.filter { it.row < highestTalent.row }.sumOf { it.rank }
                    return@booleanBinding totalPoints - 1 >= highestTalent.requiredPoints
                })
            }
        }
    }

    /** @see canDeallocateProperty */
    val canDeallocate by _canDeallocate.delegate()

    /**
     * Whether the user is able to remove talent points from this particular talent.
     *
     * This is true if removing a point from this talent will not make any later talents to become
     * non-allocatable.
     */
    fun canDeallocateProperty() = _canDeallocate.readOnlyProperty!!

    private val _canAcceptPointsBinding = maxedOutProperty().not().and(canAllocateProperty())
    private val _canAcceptPoints = ReadOnlyBooleanWrapper().also { it.bind(_canAcceptPointsBinding) }

    /** @see canAcceptPointsProperty */
    val canAcceptPoints by _canAcceptPoints.delegate()
    
    /**
     * Whether the user can actually add more points to this talent.
     */
    fun canAcceptPointsProperty() = _canAcceptPoints.readOnlyProperty!!
}