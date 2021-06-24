package org.hedbor.evan.talenttreegenerator.view

import org.hedbor.evan.talenttreegenerator.controller.TalentTreeController
import org.hedbor.evan.talenttreegenerator.model.WowClassModel
import tornadofx.View
import tornadofx.borderpane

class MainView : View("Talent Tree Generator") {
    val controller = find<TalentTreeController>()

    override val root = borderpane {
        setInScope(WowClassModel(controller.wowClass), scope)
        center<WowClassEditor>()
    }
}