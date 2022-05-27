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
        val talentContainer by cssclass()
        val talentButton by cssclass()
        val talentIcon by cssclass()
        val activeBorder by cssclass()
        val maxedOutBorder by cssclass()
        val rankCounter by cssclass()
    }

    init {
        talentContainer {
            padding = box(15.px)
        }
        talentButton {
            padding = box(0.px)
            backgroundColor += Color.TRANSPARENT
            borderWidth += box(0.px)
        }
        talentButton and disabled {
            padding = box(0.px)
            backgroundColor += Color.TRANSPARENT
            borderWidth += box(0.px)
            opacity = 1.0
        }
        talentIcon {
            backgroundSize += BackgroundSize(1.0, 1.0, true, true, false, false)
            backgroundPosition += BackgroundPosition.CENTER
            backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
        }
        activeBorder {
            padding = box(3.px)
            backgroundInsets += box(3.px)
            borderInsets += box(3.px)
            borderWidth += box(2.px)
            borderStyle += BorderStrokeStyle.SOLID
            borderColor += box(StyleConstants.ACTIVE_TALENT_BORDER_COLOR)
            borderRadius += box(7.px)
            //wowhead border size: 36px square
            //wowhead icon size: 44px square
        }
        maxedOutBorder {
            padding = box(3.px)
            backgroundInsets += box(3.px)
            borderInsets += box(3.px)
            borderWidth += box(2.px)
            borderStyle += BorderStrokeStyle.SOLID
            borderColor += box(StyleConstants.MAXED_TALENT_BORDER_COLOR)
            borderRadius += box(7.px)
        }
        rankCounter {
            textFill = StyleConstants.DESCRIPTION_TEXT_COLOR
            fontFamily = StyleConstants.UI_FONT
            fontSize = StyleConstants.SMALL_LABEL_FONT_SIZE

            backgroundColor += Color.BLACK
            backgroundRadius += box(5.px)

            padding = box(0.px, 3.px)
            borderWidth += box(1.px)
            borderColor += box(StyleConstants.DESCRIPTION_TEXT_COLOR)
            borderRadius += box(5.px)
        }
        rankCounter and disabled {
            textFill = Color.GRAY
            padding = box(0.px, 3.px)
            borderWidth += box(1.px)
            borderColor += box(Color.GRAY)
            borderRadius += box(5.px)
            opacity = 1.0
        }
    }
}