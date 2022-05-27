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

import javafx.scene.paint.Color
import tornadofx.px


@Suppress("HasPlatformType")
object StyleConstants {
    val TITLE_SIZE = 24.px
    val LARGE_LABEL_FONT_SIZE = 20.px
    val SMALL_LABEL_FONT_SIZE = 16.px

    const val UI_FONT = "Friz Quadrata TT"

    val NORMAL_TEXT_COLOR = Color.WHITE
    val DESCRIPTION_TEXT_COLOR = Color(255.0 / 255.0, 209.0 / 255.0, 0.0 / 255.0, 1.0)
    val ERROR_TEXT_COLOR = Color.RED
    val CONFIRMATION_TEXT_COLOR = Color(64.0 / 255.0, 191.0 / 255.0, 64.0 / 255.0, 1.0)

    val LIGHT_BACKGROUND_COLOR = Color(80.0 / 255.0, 80.0 / 255.0, 80.0 / 255.0, 1.0)
    val DARK_BACKGROUND_COLOR = Color(35.0 / 255.0, 35.0 / 255.0, 35.0 / 255.0, 1.0)

    val ACTIVE_TALENT_BORDER_COLOR = Color(64.0 / 255.0, 191.0 / 255.0, 64.0 / 255.0, 0.75)
    val MAXED_TALENT_BORDER_COLOR = Color(255.0 / 255.0, 209.0 / 255.0, 0.0 / 255.0, 0.75)
}