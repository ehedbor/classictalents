package org.hedbor.evan.classictalents.app.view

import org.hedbor.evan.classictalents.app.model.MainViewModel
import org.hedbor.evan.classictalents.app.model.SpecializationViewModel
import tornadofx.*

class MainView : View("Classic WoW Talent Calculator") {
    private val model by inject<MainViewModel>()

    init {
        model.onSetup()
    }

    override val root = hbox {
        val wowClass = model.classes.first()
        for (spec in wowClass.specializations) {
            val scope = Scope(SpecializationViewModel(wowClass, spec))
            this += find<SpecializationFragment>(scope)
        }
    }
}
