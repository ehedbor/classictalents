package org.hedbor.evan.classictalents.app.view

import javafx.util.Duration
import org.hedbor.evan.classictalents.app.util.setGlobalTooltipBehavior
import tornadofx.View
import tornadofx.vbox
import java.util.*

class MainView : View("Classic WoW Talent Calculator") {
    private val afflictionTalentTree = loadTalentTree("/talents/warlock.json")

    init {
        messages = ResourceBundle.getBundle("bundles.Messages")
        setGlobalTooltipBehavior(Duration.ZERO, Duration.INDEFINITE, Duration.ZERO)
    }

    override val root = vbox {
        talenttreeview(afflictionTalentTree, messages)
    }
}
