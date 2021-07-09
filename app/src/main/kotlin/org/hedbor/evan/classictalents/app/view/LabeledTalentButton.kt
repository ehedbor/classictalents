package org.hedbor.evan.classictalents.app.view

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import org.hedbor.evan.classictalents.app.view.styles.TalentStyles
import org.hedbor.evan.classictalents.common.model.Talent
import tornadofx.*


class LabeledTalentButton(val talent: Talent) : StackPane() {
    init {
        val button = talentbutton(talent)

        alignment = Pos.BOTTOM_RIGHT
        label(talent.rankProperty.stringBinding { it.toString() }) {
            addClass(TalentStyles.pointCounter)
            padding = insets(3, 0)
            isMouseTransparent = true
            enableWhen(button.allocatableProperty or button.allocatedProperty)
        }
    }
}

fun EventTarget.labeledtalentbutton(talent: Talent, op: LabeledTalentButton.() -> Unit = {}) =
    opcr(this, LabeledTalentButton(talent).apply(op))
