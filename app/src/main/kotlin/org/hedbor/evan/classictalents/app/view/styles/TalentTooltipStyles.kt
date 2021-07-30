/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2021 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.app.view.styles

import tornadofx.Stylesheet
import tornadofx.cssclass

class TalentTooltipStyles : Stylesheet() {
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