package org.hedbor.evan.classictalents.app.view

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import org.hedbor.evan.classictalents.app.view.styles.TalentButtonStyles
import org.hedbor.evan.classictalents.app.model.Talent
import tornadofx.*
import java.util.*


class LabeledTalentButton(val talent: Talent, messages: ResourceBundle) : StackPane() {
    init {
        val button = talentbutton(talent, messages)

        alignment = Pos.BOTTOM_RIGHT
        label(talent.allocatedPointsProperty().stringBinding { "$it" }) {
            addClass(TalentButtonStyles.pointCounter)
            padding = insets(3, 0)
            isMouseTransparent = true
            enableWhen(button.inactiveProperty.not())
        }
    }
}

fun EventTarget.labeledtalentbutton(talent: Talent, messages: ResourceBundle, op: LabeledTalentButton.() -> Unit = {}) =
    opcr(this, LabeledTalentButton(talent, messages).apply(op))
