package org.hedbor.evan.classictalents.app.view

import org.hedbor.evan.classictalents.app.controller.ClassicTalentsController
import tornadofx.View
import tornadofx.vbox

class MainView : View("Classic WoW Talent Calculator") {
    private val controller: ClassicTalentsController by inject()

    init {
        controller.setup()
    }

    override val root = vbox {
        talenttreeview(controller.classes.first().specializations.first())
    }
}
