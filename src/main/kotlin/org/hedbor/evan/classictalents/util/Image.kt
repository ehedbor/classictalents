package org.hedbor.evan.classictalents.util

import javafx.scene.image.Image
import javafx.scene.image.WritableImage

fun Image.grayscale(): Image {
    val result = WritableImage(width.toInt(), height.toInt())
    for (x in 0 until width.toInt()) {
        for (y in 0 until height.toInt()) {
            val pixel = pixelReader.getColor(x, y)
            result.pixelWriter.setColor(x, y, pixel.grayscale())
        }
    }
    return result
}