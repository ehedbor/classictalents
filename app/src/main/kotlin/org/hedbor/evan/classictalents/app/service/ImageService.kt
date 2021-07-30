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

package org.hedbor.evan.classictalents.app.service

import javafx.scene.image.Image
import javafx.scene.image.WritableImage


object ImageService {
    fun toGrayscale(source: Image): Image {
        val width = source.width.toInt()
        val height = source.height.toInt()
        val result = WritableImage(width, height)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = source.pixelReader.getColor(x, y)
                result.pixelWriter.setColor(x, y, pixel.grayscale())
            }
        }
        return result
    }
}