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

import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px
import java.net.URI


class SpecStyles : Stylesheet() {
    companion object {
        val specBackground by cssclass()
        val specBorder by cssclass()
        val specHeader by cssclass()
        val specTitle by cssclass()
        val specResetButton by cssclass()
    }

    init {
        specBackground {
            backgroundColor += StyleConstants.LIGHT_BACKGROUND_COLOR
            borderColor += box(StyleConstants.LIGHT_BACKGROUND_COLOR)
        }
        specBorder {
            borderColor += box(StyleConstants.LIGHT_BACKGROUND_COLOR)
            borderWidth += box(5.px)
            borderStyle += BorderStrokeStyle.SOLID
            borderRadius += box(5.px)
        }
        specHeader {
            backgroundColor += StyleConstants.LIGHT_BACKGROUND_COLOR
            borderColor += box(StyleConstants.LIGHT_BACKGROUND_COLOR)
            borderStyle += BorderStrokeStyle.SOLID
        }
        specTitle {
            textFill = StyleConstants.NORMAL_TEXT_COLOR
            fontFamily = StyleConstants.UI_FONT
            fontSize = StyleConstants.TITLE_SIZE
        }
        specResetButton {
            backgroundColor += Color.TRANSPARENT
            padding = box(5.px)
            graphic = URI("/images/other/cancel_x_20by20.png")
        }
        specResetButton and hover {
            graphic = URI("/images/other/cancel_x_20by20_hover.png")
        }
    }
}