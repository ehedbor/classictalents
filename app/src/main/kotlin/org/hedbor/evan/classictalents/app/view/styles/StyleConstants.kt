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

import javafx.scene.paint.Color
import tornadofx.Dimension
import tornadofx.px


object StyleConstants {
    val LARGE_LABEL_TEXT: Dimension<Dimension.LinearUnits> = 20.px
    val SMALL_LABEL_TEXT: Dimension<Dimension.LinearUnits> = 16.px
    const val UI_FONT: String = "Friz Quadrata TT"
    val NORMAL_COLOR: Color = Color.WHITE
    val DESCRIPTION_COLOR: Color = Color(255.0 / 255.0, 209.0 / 255.0, 0.0 / 255.0, 1.0)
    val ERROR_COLOR: Color = Color.RED
    val CONFIRMATION_COLOR: Color = Color(64.0 / 255.0, 191.0 / 255.0, 64.0 / 255.0, 1.0)

    val TALENT_ACTIVE_BORDER_COLOR: Color = Color(64.0 / 255.0, 191.0 / 255.0, 64.0 / 255.0, 0.75)
    val TALENT_MAXED_BORDER_COLOR: Color = Color(255.0 / 255.0, 209.0 / 255.0, 0.0 / 255.0, 0.75)
}