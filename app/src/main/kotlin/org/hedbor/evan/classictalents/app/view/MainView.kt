package org.hedbor.evan.classictalents.app.view

import javafx.util.Duration
import org.hedbor.evan.classictalents.app.controller.ClassicTalentsController
import org.hedbor.evan.classictalents.app.util.setGlobalTooltipBehavior
import tornadofx.View
import tornadofx.vbox
import java.util.*

class MainView : View("Classic WoW Talent Calculator") {
    private val controller: ClassicTalentsController by inject()

    init {
        setGlobalTooltipBehavior(Duration.ZERO, Duration.INDEFINITE, Duration.ZERO)
        controller.load("/talents/warlock.json", "bundles.warlock")
        messages = ResourceBundle.getBundle("bundles.Messages")
    }

    override val root = vbox {
        //talenttreeview(controller.wowClass.specializations.first(), messages)
    }
}
