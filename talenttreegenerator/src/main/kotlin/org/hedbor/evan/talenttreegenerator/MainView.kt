package org.hedbor.evan.talenttreegenerator

import javafx.scene.control.ButtonBar
import org.hedbor.evan.talenttreegenerator.model.WowClassModel
import tornadofx.*

class MainView : View("New Class") {
    val model: WowClassModel by inject()

    override val root = form {
        fieldset(title) {
            field("Class Name") {
                textfield(model.displayName).required()
            }
            field("Translation Key") {
                textfield(model.translationKey) {
                    isEditable = false
                }
            }
            field("Spec 1 Name") {
                textfield()
            }
            field("Spec 2 Name") {
                textfield()
            }
            field("Spec 3 Name") {
                textfield()
            }

            buttonbar {
                button("Next", ButtonBar.ButtonData.OK_DONE) {
                    enableWhen(model.valid)
                    action {
                        model.commit()
                    }
                }
            }
        }
    }
}