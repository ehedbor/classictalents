package org.hedbor.evan.classictalents.app.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.css.PseudoClass
import javafx.event.EventTarget
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.layout.StackPane
import org.hedbor.evan.classictalents.app.util.generateGrayscaleImage
import org.hedbor.evan.classictalents.app.view.styles.TalentStyles
import org.hedbor.evan.classictalents.common.model.Talent
import tornadofx.*


/**
 * Creates a
 */
class TalentButton(val talent: Talent) : Button() {
    companion object {
        private val BORDER_IMAGE                 = Image("/images/Icon/large/border/default.png")
        private val BORDER_HILITE_HOVER_IMAGE    = Image("/images/Icon/large/hilite/hilite.png")
        private val BORDER_HILITE_ENABLED_IMAGE  = Image("/images/Icon/large/hilite/enabled.png")
        private val BORDER_HILITE_MAX_RANK_IMAGE = Image("/images/Icon/large/hilite/max_rank.png")

        private val ALLOCATABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("talentAllocatable")
        private val ALLOCATED_PSEUDO_CLASS = PseudoClass.getPseudoClass("talentAllocated")
        private val MAXED_OUT_PSEUDO_CLASS = PseudoClass.getPseudoClass("talentMaxedOut")
    }

    /**
     * Whether or not the prerequisite talent rows have been filed out.
     */
    val talentRowUnlockedProperty = SimpleBooleanProperty(this, "isTalentRowUnlocked", true)
    var isTalentRowUnlocked by talentRowUnlockedProperty

    /**
     * Whether or not this talent's prerequisite has been maxed out.
     */
    val prerequisiteMaxedOutProperty = SimpleBooleanProperty(this, "isPrerequisiteMaxedOut", true)
    var isPrerequisiteMaxedOut by prerequisiteMaxedOutProperty

    /**
     * Whether or not the user still has unassigned talent points.
     */
    val hasUnassignedTalentPointsProperty = SimpleBooleanProperty(this, "hasUnassignedTalentPoints", true)
    var hasUnassignedTalentPoints by hasUnassignedTalentPointsProperty

    /**
     * Whether or not the user is able to remove talent points from this particular talent.
     */
    val canRemovePointsProperty = SimpleBooleanProperty(this, "canRemovePoints", true)
    var canRemovePoints by canRemovePointsProperty

    /**
     * Whether or not talent points are able to be allocated into this talent. This is true if:
     * A) This talent's row is unlocked,
     * B) This talent's [prerequisite][Talent.prerequisite] (if any) is maxed out, and
     * C) The user still has unassigned talent points.
     *
     * Note that the value of this property is independent of how many points have actually
     * been allocated into this talent.
     */
    val allocatableProperty = talentRowUnlockedProperty and prerequisiteMaxedOutProperty and hasUnassignedTalentPointsProperty
    val isAllocatable by allocatableProperty

    /**
     * True if at least one talent point has been allocated into this talent.
     */
    val allocatedProperty = talent.rankProperty ge 1
    val isAllocated by allocatedProperty

    /**
     * True if [Talent.maxRank] points have been allocated into this talent.
     */
    val maxedOutProperty = talent.rankProperty ge talent.maxRankProperty
    val isMaxedOut by maxedOutProperty

    /**
     * The border image that should be used, or null if none are applicable.
     */
    private val borderHiliteImageProperty = objectBinding(allocatableProperty, allocatedProperty, maxedOutProperty) {
        if (isMaxedOut) {
            BORDER_HILITE_MAX_RANK_IMAGE
        } else if (isAllocatable || isAllocated) {
            BORDER_HILITE_ENABLED_IMAGE
        } else {
            null
        }
    }

    private val normalBackgroundImage = Image(talent.icon)
    private val grayscaleBackgroundImage = generateGrayscaleImage(normalBackgroundImage)

    /**
     * The current background image.
     */
    private val backgroundImageProperty = objectBinding(allocatableProperty, allocatedProperty, maxedOutProperty) {
        if (isMaxedOut || (isAllocatable || isAllocated)) {
            normalBackgroundImage
        } else {
            grayscaleBackgroundImage
        }
    }

    init {
        addClass(TalentStyles.talentButton)
        padding = insets(0)
        graphic = StackPane().apply {
            imageview(backgroundImageProperty) {
                addClass(TalentStyles.talentButtonIcon)
            }
            imageview(BORDER_IMAGE) {
                addClass(TalentStyles.talentButtonIcon)
            }
            imageview(BORDER_HILITE_HOVER_IMAGE) {
                addClass(TalentStyles.talentButtonIcon)
                visibleWhen(this@TalentButton.hoverProperty())
            }
            /*imageview(borderHiliteImageProperty) {
                addClass(TalentStyles.talentButtonIcon)
                visibleWhen { imageProperty().isNotNull }
            }*/
            pane {
                addClass(TalentStyles.talentButtonBorder)
            }
        }
        setOnMouseClicked { event ->
            if (event.button == MouseButton.PRIMARY && isAllocatable && !isMaxedOut) {
                talent.rank++
            } else if (event.button == MouseButton.SECONDARY && isAllocated && canRemovePoints) {
                talent.rank--
            }
        }
        Tooltip.install(this, TalentButtonTooltip(talent))
    }

    override fun computeMinWidth(height: Double): Double {
        return computePrefWidth(height)
    }

    override fun computeMinHeight(width: Double): Double {
        return computePrefHeight(width)
    }

    override fun computePrefWidth(height: Double): Double {
        return BORDER_IMAGE.width + snappedLeftInset() + snappedRightInset()
    }

    override fun computePrefHeight(width: Double): Double {
        return BORDER_IMAGE.height + snappedTopInset() + snappedBottomInset()
    }

    override fun computeMaxWidth(height: Double): Double {
        return  computePrefWidth(height)
    }

    override fun computeMaxHeight(width: Double): Double {
        return computePrefHeight(width)
    }
}

fun EventTarget.talentbutton(talent: Talent, op: TalentButton.() -> Unit = {}) =
    opcr(this, TalentButton(talent).apply(op))