package org.hedbor.evan.classictalents.app.view.styles

import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.paint.Color
import tornadofx.*

class TalentStyles : Stylesheet() {
    companion object {
        val talentButton by cssclass()
        val inactive by csspseudoclass()
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
        talentButton and inactive {
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
            textFill = StyleConstants.DESCRIPTION_COLOR
            fontFamily = StyleConstants.UI_FONT
            fontSize = StyleConstants.SMALL_LABEL_TEXT
            backgroundColor += Color.BLACK
            backgroundRadius += box(5.px)

            borderWidth += box(1.px)
            borderColor += box(StyleConstants.DESCRIPTION_COLOR)
            borderRadius += box(5.px)
        }
        pointCounter and disabled {
            textFill = Color.GRAY
            borderWidth += box(1.px)
            borderColor += box(Color.GRAY)
            borderRadius += box(5.px)
            opacity = 1.0
        }
    }
}