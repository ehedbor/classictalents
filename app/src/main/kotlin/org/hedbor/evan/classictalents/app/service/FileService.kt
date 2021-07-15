package org.hedbor.evan.classictalents.app.service

import org.hedbor.evan.classictalents.common.model.WowClass
import org.hedbor.evan.classictalents.common.serialization.ClassicTalentsSerializer
import tornadofx.FX
import java.io.IOException
import java.util.*


object FileService {
    fun loadWowClass(path: String): WowClass {
        val stream = this::class.java.getResourceAsStream(path)
            ?: throw IOException("Fatal Error: Could not load resource at \"$path\".")
        return ClassicTalentsSerializer.loadClassAsStream(stream)
    }

    fun loadBundle(bundlePath: String): ResourceBundle {
        return ResourceBundle.getBundle(bundlePath, FX.locale)
    }
}