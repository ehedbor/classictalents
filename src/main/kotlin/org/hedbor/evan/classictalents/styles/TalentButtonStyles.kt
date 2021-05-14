package org.hedbor.evan.classictalents.styles

import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class TalentButtonStyles : Stylesheet() {
    companion object {
        val talentButton by cssclass()
        val talentButtonIcon by cssclass()
        val pointCounter by cssclass()

        val tooltipTitle by cssclass()
        val tooltipSubtitle by cssclass()
        val tooltipDescription by cssclass()
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
            backgroundSize += BackgroundSize(1.0, 1.0, true, true, false, false)
            backgroundPosition += BackgroundPosition.CENTER
            backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
        }
        pointCounter {
            textFill = Color.GOLDENROD
            fontFamily = "Friz Quadrata TT"
            fontSize = 14.px
            backgroundColor += Color.BLACK
            backgroundRadius += box(5.px)

            borderWidth += box(1.px)
            borderColor += box(Color.DARKGOLDENROD)
            borderRadius += box(5.px)
        }
        pointCounter and disabled {
            opacity = 1.0
        }
    }
}