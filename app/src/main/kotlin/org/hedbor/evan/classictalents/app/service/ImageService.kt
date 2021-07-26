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