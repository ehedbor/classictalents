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
            // specified in code because i couldn't figure out how to dynamically load the background
            // without overriding the existing background css
            //backgroundPosition += BackgroundPosition.CENTER
            //backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
            //backgroundSize += BackgroundSize(1.0, 1.0, true, true, false, false)
//            borderColor += box(StyleConstants.LIGHT_BACKGROUND_COLOR)
//            borderWidth += box(5.px)
//            borderStyle += BorderStrokeStyle.SOLID
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
            //borderWidth += box(5.px)
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
//        specResetButton and hover {
//            backgroundColor += Color.TRANSPARENT
//            padding = box(1.px)
//            graphic = URI("/images.other.cancel_x_20by20_hover.png")
//        }
    }
}