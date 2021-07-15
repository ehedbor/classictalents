package org.hedbor.evan.classictalents.app.controller

import javafx.application.Platform
import javafx.util.Duration
import org.hedbor.evan.classictalents.app.util.setGlobalTooltipBehavior
import org.hedbor.evan.classictalents.common.model.WowClass
import org.hedbor.evan.classictalents.common.serialization.ClassicTalentsSerializer
import tornadofx.Controller
import tornadofx.FX
import tornadofx.observableListOf
import java.io.IOException
import java.util.*
import kotlin.system.exitProcess


class ClassicTalentsController : Controller() {
    val classes = observableListOf<WowClass>()

    fun setup() {
        setGlobalTooltipBehavior(Duration.ZERO, Duration.INDEFINITE, Duration.ZERO)
        classes += loadClass("/talents/warlock.json")
        // this causes a crash if a key is not found
        FX.messages = loadBundle("bundles.Messages")
    }

    fun quit() {
        Platform.exit()
        exitProcess(0)
    }

    private fun loadClass(path: String): WowClass {
        val stream = this::class.java.getResourceAsStream(path) ?: throw IOException("Fatal Error: Could not load resource at \"$path\".")
        return ClassicTalentsSerializer.loadClassAsStream(stream)
    }

    private fun loadBundle(bundlePath: String): ResourceBundle {
        return ResourceBundle.getBundle(bundlePath, FX.locale)
    }
}