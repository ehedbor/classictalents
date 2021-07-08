package org.hedbor.evan.classictalents.app.controller

import javafx.application.Platform
import org.hedbor.evan.classictalents.common.model.WowClass
import tornadofx.Controller
import kotlin.system.exitProcess


class ClassicTalentsController : Controller() {
    var wowClass = WowClass()
        private set

    fun load(dataFile: String, resourceBundlePath: String) {
        //wowClass = ClassicTalentsSerializer.loadClassAsStream(dataFile, resourceBundlePath)
    }

    fun quit() {
        Platform.exit()
        exitProcess(0)
    }
}