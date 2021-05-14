package org.hedbor.evan.classictalents.styles

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.px


class TalentButtonTooltipStyles : Stylesheet() {
    companion object {
        val tooltipTitle by cssclass()
        val tooltipSubtitle by cssclass()
        val tooltipDescription by cssclass()
    }

    init {
        tooltipTitle {
            textFill = Color.WHITE
            fontFamily = "Friz Quadrata TT"
            fontSize = 24.px
        }
        tooltipSubtitle {
            textFill = Color.WHITE
            fontFamily = "Friz Quadrata TT"
            fontSize = 20.px
        }
        tooltipDescription {
            textFill = Color.GOLD
            fontFamily = "Friz Quadrata TT"
            fontSize = 20.px
        }
    }
}