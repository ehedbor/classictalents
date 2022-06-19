package org.hedbor.evan.classictalents.control

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.hedbor.evan.classictalents.model.MainModel
import org.hedbor.evan.classictalents.model.WowClass
import org.hedbor.evan.classictalents.util.booleanBinding
import org.hedbor.evan.classictalents.util.stringBinding

class WowClassView(private val model: WowClass /*MainModel*/) {
    @FXML private lateinit var specsPane: HBox
    @FXML private lateinit var footer: VBox
    @FXML private lateinit var requiredLevelLabel: Label
    @FXML private lateinit var remainingPointsLabel: Label

    @FXML
    private fun initialize() {
        // TODO: reuse view for different models. see MainViewController for reasoning
        setClass(model)

//        footer.visibleProperty().bind(model.selectedClassProperty().booleanBinding { it != null })
//
//        if (model.selectedClass != null) {
//            setClass(model.selectedClass!!)
//        }
//
//        model.selectedClassProperty().addListener { _, _, cls ->
//            specsPane.children.clear()
//            requiredLevelLabel.textProperty().unbind()
//            remainingPointsLabel.textProperty().unbind()
//
//            if (cls != null) setClass(cls)
//        }
    }

    private fun setClass(wowClass: WowClass) {
        wowClass.specializations
            .map { SpecializationView(it) }
            .toCollection(specsPane.children)

        requiredLevelLabel.textProperty().bind(
            wowClass.allocatedPointsProperty().stringBinding { allocated ->
                if (allocated as Int > 0)
                    "Required Level: ${allocated + 9}"
                else
                    "Required Level: \u2013" // en dash
            })

        remainingPointsLabel.textProperty().bind(
            wowClass.allocatedPointsProperty().stringBinding { allocated ->
                // TODO: max points depends on expansion
                val maxPoints = 51
                val remaining = maxPoints - (allocated as Int)
                "Remaining Points: $remaining"
            }
        )
    }
}