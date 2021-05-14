package org.hedbor.evan.classictalents

import javafx.util.Duration
import org.hedbor.evan.classictalents.controls.talenttreeview
import org.hedbor.evan.classictalents.util.loadTalentTree
import org.hedbor.evan.classictalents.util.setGlobalTooltipBehavior
import tornadofx.View
import tornadofx.vbox
import java.util.*

class MainView : View("Classic WoW Talent Calculator") {
    val afflictionTalentTree = loadTalentTree("/talents/warlock.json")

    init {
        messages = ResourceBundle.getBundle("bundles.Messages")
        setGlobalTooltipBehavior(Duration.ZERO, Duration.INDEFINITE, Duration.ZERO)
    }

    override val root = vbox {
        talenttreeview(afflictionTalentTree, messages)
    }
}
