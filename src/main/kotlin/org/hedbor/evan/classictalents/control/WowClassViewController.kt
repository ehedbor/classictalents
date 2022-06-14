package org.hedbor.evan.classictalents.control

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.model.MainModel
import org.hedbor.evan.classictalents.model.WowClass
import org.hedbor.evan.classictalents.util.stringBinding

class WowClassViewController(private val model: MainModel) {
    @FXML private lateinit var specsPane: HBox
    @FXML private lateinit var requiredLevelLabel: Label
    @FXML private lateinit var remainingPointsLabel: Label

    @FXML
    private fun initialize() {
        if (model.selectedClass != null) {
            setClass(model.selectedClass!!)
        }
        model.selectedClassProperty().addListener { _, _, cls ->
            specsPane.children.clear()
            requiredLevelLabel.textProperty().unbind()
            remainingPointsLabel.textProperty().unbind()

            if (cls != null) setClass(cls)
        }
    }

    private fun setClass(wowClass: WowClass) {
        wowClass.specializations
            .map { SpecializationView(it) }
            .toCollection(specsPane.children)

        requiredLevelLabel.textProperty().bind(
            wowClass.allocatedPointsProperty().stringBinding { allocated ->
                val level = 10 + (allocated as Int)
                "Required Level: $level"
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