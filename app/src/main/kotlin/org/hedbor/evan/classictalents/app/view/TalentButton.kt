package org.hedbor.evan.classictalents.app.view

import javafx.beans.property.BooleanProperty
import javafx.beans.property.BooleanPropertyBase
import javafx.css.PseudoClass
import javafx.event.EventTarget
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.StackPane
import org.hedbor.evan.classictalents.app.model.Talent
import org.hedbor.evan.classictalents.app.util.generateGrayscaleImage
import tornadofx.*
import java.util.*


class TalentButton(val talent: Talent, messages: ResourceBundle) : ToggleButton() {
    companion object {
        private const val BORDER_IMAGE = "/images/Icon/large/border/default.png"
        private const val BORDER_HILITE_HOVER_IMAGE = "/images/Icon/large/hilite/hilite.png"
        private const val BORDER_HILITE_ACTIVE_IMAGE = "/images/Icon/large/hilite/enabled.png"
        private const val BORDER_HILITE_MAX_RANK_IMAGE = "/images/Icon/large/hilite/max_rank.png"

        private val INACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("inactive")
    }

    val inactiveProperty: BooleanProperty = object : BooleanPropertyBase(false) {
        override fun invalidated() {
            pseudoClassStateChanged(INACTIVE_PSEUDO_CLASS, get())
        }

        override fun getBean(): Any = this@TalentButton

        override fun getName(): String = "inactive"
    }

    var isInactive by inactiveProperty

    private val normalBackgroundImage = Image(talent.icon)
    private val grayscaleBackgroundImage = generateGrayscaleImage(normalBackgroundImage)
    private val backgroundImageView: ImageView

    // used to compute size
    private val borderImage = Image(BORDER_IMAGE)

    init {
        addClass(org.hedbor.evan.classictalents.app.view.styles.TalentButtonStyles.talentButton)
        padding = insets(0)
        inactiveProperty.bind(talent.shouldBeActive.not())

        graphic = StackPane().apply {
            backgroundImageView = imageview(getAppropriateBackgroundImage(isInactive)) {
                addClass(org.hedbor.evan.classictalents.app.view.styles.TalentButtonStyles.talentButtonIcon)
            }
            imageview(borderImage) {
                addClass(org.hedbor.evan.classictalents.app.view.styles.TalentButtonStyles.talentButtonIcon)
            }
            imageview(BORDER_HILITE_HOVER_IMAGE) {
                addClass(org.hedbor.evan.classictalents.app.view.styles.TalentButtonStyles.talentButtonIcon)
                visibleProperty().bind(this@TalentButton.hoverProperty())
            }
            imageview(BORDER_HILITE_ACTIVE_IMAGE) {
                addClass(org.hedbor.evan.classictalents.app.view.styles.TalentButtonStyles.talentButtonIcon)
                visibleProperty().bind(talent.allocatedPointsProperty().booleanBinding(this@TalentButton.inactiveProperty) { allocatedPoints ->
                    allocatedPoints != talent.maxRank && !this@TalentButton.isInactive
                })
            }
            imageview(BORDER_HILITE_MAX_RANK_IMAGE) {
                addClass(org.hedbor.evan.classictalents.app.view.styles.TalentButtonStyles.talentButtonIcon)
                visibleProperty().bind(talent.allocatedPointsProperty().booleanBinding { it == talent.maxRank })
            }
        }
        setOnMouseClicked { event ->
            if (isInactive) return@setOnMouseClicked
            if (event.button == MouseButton.PRIMARY && talent.allocatedPoints < talent.maxRank) {
                talent.allocatedPoints++
            } else if (event.button == MouseButton.SECONDARY && talent.canRemovePoints.get()) {
                talent.allocatedPoints--
            }
            isSelected = talent.allocatedPoints == talent.maxRank
        }
        inactiveProperty.addListener(ChangeListener { _, _, inactive ->
            backgroundImageView.image = getAppropriateBackgroundImage(inactive)
        })
        Tooltip.install(this, TalentButtonTooltip(talent, messages))
    }

    override fun computeMinWidth(height: Double): Double {
        return computePrefWidth(height)
    }

    override fun computeMinHeight(width: Double): Double {
        return computePrefHeight(width)
    }

    override fun computePrefWidth(height: Double): Double {
        return borderImage.width + snappedLeftInset() + snappedRightInset()
    }

    override fun computePrefHeight(width: Double): Double {
        return borderImage.height + snappedTopInset() + snappedBottomInset()
    }

    override fun computeMaxWidth(height: Double): Double {
        return  computePrefWidth(height)
    }

    override fun computeMaxHeight(width: Double): Double {
        return computePrefHeight(width)
    }

    private fun getAppropriateBackgroundImage(inactive: Boolean): Image {
        return if (inactive) grayscaleBackgroundImage else normalBackgroundImage
    }
}

fun EventTarget.talentbutton(talent: Talent, messages: ResourceBundle, op: TalentButton.() -> Unit = {}) =
    opcr(this, TalentButton(talent, messages).apply(op))