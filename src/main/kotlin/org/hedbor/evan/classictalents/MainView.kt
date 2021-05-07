package org.hedbor.evan.classictalents

import tornadofx.*
import java.net.URI
import java.util.*

class MainView : View("Classic WoW Talent Calculator") {
    init {
        messages = ResourceBundle.getBundle("bundles.Messages")
    }

    override val root = gridpane {
        style {
            backgroundImage  += URI.create("/images/backgrounds/warlock_affliction.jpg")
        }
    }
}