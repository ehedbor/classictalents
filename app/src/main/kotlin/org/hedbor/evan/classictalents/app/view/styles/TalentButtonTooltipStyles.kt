package org.hedbor.evan.classictalents.app.view.styles

import tornadofx.Stylesheet
import tornadofx.cssclass

class TalentButtonTooltipStyles : Stylesheet() {
    companion object {
        val tooltipTitle by cssclass()
        val tooltipSubtitle by cssclass()
        val tooltipDescription by cssclass()
        val tooltipError by cssclass()
        val tooltipConfirmation by cssclass()
    }

    init {
        tooltipTitle {
            textFill = StyleConstants.NORMAL_COLOR
            fontFamily = StyleConstants.UI_FONT
            fontSize = StyleConstants.LARGE_LABEL_TEXT
        }
        tooltipSubtitle {
            textFill = StyleConstants.NORMAL_COLOR
            fontFamily = StyleConstants.UI_FONT
            fontSize = StyleConstants.SMALL_LABEL_TEXT
        }
        tooltipDescription {
            textFill = StyleConstants.DESCRIPTION_COLOR
            fontFamily = StyleConstants.UI_FONT
            fontSize = StyleConstants.SMALL_LABEL_TEXT
        }
        tooltipError {
            textFill = StyleConstants.ERROR_COLOR
            fontFamily = StyleConstants.UI_FONT
            fontSize = StyleConstants.SMALL_LABEL_TEXT
        }
        tooltipConfirmation {
            textFill = StyleConstants.CONFIRMATION_COLOR
            fontFamily = StyleConstants.UI_FONT
            fontSize = StyleConstants.SMALL_LABEL_TEXT
        }
    }
}