package org.hedbor.evan.classictalents.model

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyIntegerProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.scene.image.Image
import org.hedbor.evan.classictalents.util.booleanBinding
import org.hedbor.evan.classictalents.util.delegate


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

    private val _maxRank = SimpleIntegerProperty()
    var maxRank by _maxRank.delegate()
    fun maxRankProperty() = _maxRank

    private val _rank = SimpleIntegerProperty()
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

    private val _requiredPoints = SimpleIntegerProperty().apply {
        bind(rowProperty().multiply(5))
    }
    val requiredPoints by _requiredPoints.delegate()
    fun requiredPointsProperty() = _requiredPoints as ReadOnlyIntegerProperty

    private val _isMaxedOut = SimpleBooleanProperty().also { prop ->
        prop.bind(rankProperty().isEqualTo(maxRankProperty()))
    }
    val isMaxedOut by _isMaxedOut.delegate()
    fun maxedOutProperty() = _isRowUnlocked as ReadOnlyBooleanProperty

    private val _isRowUnlocked = SimpleBooleanProperty().also { prop ->
        specializationProperty().addListener { _, oldSpec, newSpec ->
            if (oldSpec != null) prop.unbind()
            if (newSpec != null) {
                prop.bind(booleanBinding(requiredPointsProperty(), newSpec.allocatedPointsProperty()) {
                    newSpec.allocatedPoints >= requiredPoints
                })
            } else {
                prop.value = false
            }
        }
    }
    val isRowUnlocked by _isRowUnlocked.delegate()
    fun rowUnlockedProperty() = _isRowUnlocked as ReadOnlyBooleanProperty

    private val _canAllocate = SimpleBooleanProperty().also { canAllocProp ->
        // TODO: make an improved .select{} method, this is ridiculous
        val prereqMaxedOut = SimpleBooleanProperty(true).also { prop ->
            prerequisiteProperty().addListener { _, old, new ->
                if (old != null) { prop.unbind() }
                if (new != null) {
                    // WTF: booleanBinding() doesn't work but BooleanBinding does
                    // WHY????
                    prop.bind(object : BooleanBinding() {
                        init { bind(new.maxedOutProperty()) }

                        override fun computeValue() = new.isMaxedOut
                    })
                } else {
                    prop.value = true
                }
            }
        }
        val hasUnassignedPoints = SimpleBooleanProperty(true).also { prop ->
            specializationProperty().addListener { _, oldSpec, newSpec ->
                if (oldSpec != null) prop.unbind()
                if (newSpec != null) {
                    prop.bind(SimpleBooleanProperty().also { innerProp ->
                        newSpec.wowClassProperty().addListener { _, oldClass, newClass ->
                            if (oldClass != null) innerProp.unbind()
                            if (newClass != null) {
                                innerProp.bind(newClass.hasUnassignedPointsProperty())
                            } else {
                                innerProp.value = true
                            }
                        }
                    })
                } else {
                    prop.value = true
                }
            }
        }

        canAllocProp.bind(prereqMaxedOut.and(rowUnlockedProperty()).and(hasUnassignedPoints))
    }

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

    private val _canDeallocate = SimpleBooleanProperty().apply {
        // TODO: is this all we need to bind?
        specializationProperty().addListener { _, old, new ->
            if (old != null) unbind()
            if (new != null) {
                bind(new.talentsProperty().booleanBinding { talents ->
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
    fun canDeallocateProperty() = _canDeallocate as ReadOnlyBooleanProperty

    private val _canAcceptPoints = SimpleBooleanProperty().also {
        it.bind(booleanBinding(canAllocateProperty(), maxedOutProperty(), rankProperty()) {
            !isMaxedOut && (canAllocate /*|| rank >= 1*/)
        })
    }

    /** @see canAcceptPointsProperty */
    val canAcceptPoints by _canAcceptPoints.delegate()
    
    /**
     * Whether the user can actually add more points to this talent.
     */
    fun canAcceptPointsProperty() = _canAcceptPoints as ReadOnlyBooleanProperty
}