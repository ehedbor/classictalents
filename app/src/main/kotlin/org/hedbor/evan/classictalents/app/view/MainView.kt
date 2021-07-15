package org.hedbor.evan.classictalents.app.view

import org.hedbor.evan.classictalents.app.model.MainViewModel
import org.hedbor.evan.classictalents.app.model.SpecializationViewModel
import tornadofx.*

class MainView : View("Classic WoW Talent Calculator") {
    private val model by inject<MainViewModel>()

    init {
        model.onSetup()
    }

    override val root = vbox {
        val `class` = model.classes.first()
        val spec = `class`.specializations.first()
        val scope = Scope(SpecializationViewModel(`class`, spec))
        this += find<SpecializationFragment>(scope)
    }
}
