package org.hedbor.evan.talenttreegenerator

import org.hedbor.evan.talenttreegenerator.model.SpecializationModel
import org.hedbor.evan.talenttreegenerator.model.Talent
import tornadofx.*


class SpecializationView : View("New Specialization") {
    val model: SpecializationModel by inject()

    override val root = borderpane {
        center {
            tableview(model.talents) {
                column("Display Name", Talent::displayNameProperty)
                readonlyColumn("Translation Key", Talent::translationKeyProperty)
            }
        }
    }
}