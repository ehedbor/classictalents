package org.hedbor.evan.classictalents.app.view.styles

import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class TalentStyles : Stylesheet() {
    companion object {
        val talentButton by cssclass()
        val talentButtonIcon by cssclass()
        val talentButtonBorder by cssclass()
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
        talentButtonBorder {
            borderInsets += box(4.px)
            borderWidth += box(2.px)
            borderStyle += BorderStrokeStyle.SOLID
            borderColor += box(StyleConstants.TALENT_ACTIVE_BORDER_COLOR)
            borderRadius += box(5.px)
            borderWidth += box(36.px)
            //wowhead border size: 36px square
            //wowhead icon size: 44px square
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