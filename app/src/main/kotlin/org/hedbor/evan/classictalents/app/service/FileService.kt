package org.hedbor.evan.classictalents.app.service

import org.hedbor.evan.classictalents.app.util.MultiResourceBundle
import org.hedbor.evan.classictalents.common.model.WowClass
import org.hedbor.evan.classictalents.common.serialization.ClassicTalentsSerializer
import java.io.IOException
import java.util.*


object FileService {
    fun loadClasses(vararg paths: String): List<WowClass> {
        return paths.map { path ->
            val stream = this::class.java.getResourceAsStream(path)
                ?: throw IOException("Fatal Error: Could not load resource at \"$path\".")
            ClassicTalentsSerializer.loadClassAsStream(stream)
        }
    }

    fun loadBundles(baseName: String, vararg delegateBaseNames: String): ResourceBundle {
        val control = MultiResourceBundle.Control(delegateBaseNames)
        return ResourceBundle.getBundle(baseName, control)
    }
}
