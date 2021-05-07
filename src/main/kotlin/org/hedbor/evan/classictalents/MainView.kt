package org.hedbor.evan.classictalents

import org.hedbor.evan.classictalents.controls.TalentTreeView
import org.hedbor.evan.classictalents.controls.talentbutton
import org.hedbor.evan.classictalents.controls.talenttreeview
import org.hedbor.evan.classictalents.util.loadTalentTree
import tornadofx.*
import java.net.URI
import java.util.*

class MainView : View("Classic WoW Talent Calculator") {
    val afflictionTalentTree = loadTalentTree("/talents/warlock.json")

    init {
        messages = ResourceBundle.getBundle("bundles.Messages")
    }

    override val root = vbox {
        talenttreeview(afflictionTalentTree, messages)
    }
}
