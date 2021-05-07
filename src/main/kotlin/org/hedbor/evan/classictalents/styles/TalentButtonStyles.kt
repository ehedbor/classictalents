package org.hedbor.evan.classictalents.styles

import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import tornadofx.*

class TalentButtonStyles : Stylesheet() {
    companion object {
        val talentButton by cssclass()
        val talentButtonIcon by cssclass()
        val pointCounter by cssclass()
    }

    init {
        //active
        talentButton {
            // remove original border
            backgroundColor += Color.TRANSPARENT
            borderWidth += box(0.px)
        }
        talentButton and hover {
            backgroundColor += Color.TRANSPARENT
            borderWidth += box(0.px)
        }
        talentButton and armed {
            backgroundColor += Color.TRANSPARENT
            borderWidth += box(0.px)
        }
        talentButton and focused {
            backgroundColor += Color.TRANSPARENT
            borderWidth += box(0.px)
        }
        talentButton and hover {
            backgroundColor += Color.TRANSPARENT
            borderWidth += box(0.px)
        }
        talentButton and selected {
            backgroundColor += Color.TRANSPARENT
            borderWidth += box(0.px)
        }
        talentButton and selected and focused {
            backgroundColor += Color.TRANSPARENT
            borderWidth += box(0.px)
        }
        talentButton and disabled {
            backgroundColor += Color.TRANSPARENT
            borderWidth += box(0.px)
            opacity = 1.0
        }

        talentButtonIcon {
            // image
            //backgroundSize += BackgroundSize(56.0, 56.0, false, false, false, false)
            // border
            //backgroundSize += BackgroundSize(68.0, 68.0, false, false, false, false)
            backgroundPosition += BackgroundPosition.CENTER
            backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
        }
        pointCounter {
            textFill = Color.WHITE
            fontFamily = "Arial"
            fontSize = 14.px
            backgroundColor += Color.BLACK
        }
        pointCounter and disabled {
            opacity = 1.0
        }
    }
}