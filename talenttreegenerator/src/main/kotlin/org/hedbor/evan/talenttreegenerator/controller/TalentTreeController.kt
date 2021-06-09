package org.hedbor.evan.talenttreegenerator.controller

import org.hedbor.evan.talenttreegenerator.model.WowClass
import org.hedbor.evan.talenttreegenerator.model.WowClassModel
import tornadofx.Controller


class TalentTreeController : Controller() {
    val wowClass = WowClass(displayName = "New Class")
    val model = WowClassModel()

    init {
        model.item = wowClass
    }
}