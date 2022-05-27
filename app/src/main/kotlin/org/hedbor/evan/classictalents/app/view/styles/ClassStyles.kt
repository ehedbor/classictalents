/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2022 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.app.view.styles

import javafx.scene.layout.BorderStrokeStyle
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px


class ClassStyles : Stylesheet() {
    companion object {
        val classBackground by cssclass()
        val classFooter by cssclass()
        val classDescriptionText by cssclass()
    }

    init {
        classBackground {
            backgroundColor += StyleConstants.DARK_BACKGROUND_COLOR
        }
        classFooter {
            backgroundColor += StyleConstants.LIGHT_BACKGROUND_COLOR
            borderColor += box(StyleConstants.LIGHT_BACKGROUND_COLOR)
            borderWidth += box(5.px)
            backgroundRadius += box(10.px)
            borderRadius += box(10.px)
            borderStyle += BorderStrokeStyle.SOLID
        }
        classDescriptionText {
            textFill = StyleConstants.NORMAL_TEXT_COLOR
            fontFamily = StyleConstants.UI_FONT
            fontSize = StyleConstants.LARGE_LABEL_FONT_SIZE
        }
    }
}