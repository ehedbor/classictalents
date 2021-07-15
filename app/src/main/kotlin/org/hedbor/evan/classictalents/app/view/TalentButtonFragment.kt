package org.hedbor.evan.classictalents.app.view

import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import org.hedbor.evan.classictalents.app.model.TalentButtonViewModel
import org.hedbor.evan.classictalents.app.view.styles.TalentStyles
import tornadofx.*


class TalentButtonFragment : Fragment() {
    private val model by inject<TalentButtonViewModel>()

    override val root = stackpane {
        addClass(TalentStyles.talentContainer)
        button {
            addClass(TalentStyles.talentButton)

            prefWidth = model.buttonWidth
            prefHeight = model.buttonHeight

            minWidth = prefWidth
            minHeight = prefHeight
            maxWidth = prefWidth
            maxHeight = prefHeight

            setOnMouseClicked(model::onMouseClicked)

            tooltip = model.tooltip
            graphic = StackPane().apply {
                imageview(model.backgroundImage) {
                    addClass(TalentStyles.talentIcon)
                }
                imageview(model.borderImage) {
                    addClass(TalentStyles.talentIcon)
                }
                imageview(model.borderHiliteImage) {
                    addClass(TalentStyles.talentIcon)
                    visibleWhen(this@button.hoverProperty())
                }
                region {
                    toggleClass(TalentStyles.activeBorder, model.canAcceptPoints)
                    toggleClass(TalentStyles.maxedOutBorder, model.isMaxedOut)
                }
            }
        }
        alignment = Pos.BOTTOM_RIGHT
        label(model.rankCounterText) {
            addClass(TalentStyles.rankCounter)
            isMouseTransparent = true
            enableWhen(model.isAllocatable or model.hasBeenAllocated)
        }
    }
}