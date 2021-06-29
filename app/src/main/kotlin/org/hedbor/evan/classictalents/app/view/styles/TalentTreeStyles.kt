package org.hedbor.evan.classictalents.app.view.styles

import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import tornadofx.*


class TalentTreeStyles : Stylesheet() {
    companion object {
        val talentTreeBackground by cssclass()
    }

    init {
        talentTreeBackground {
            backgroundPosition += BackgroundPosition.CENTER
            backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
            backgroundSize += BackgroundSize(1.0, 1.0, true, true, false, false)
        }
    }
}