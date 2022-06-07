package org.hedbor.evan.classictalents.controller

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.model.WowClass

class WowClassController {
    lateinit var wowClass: WowClass

    @FXML private lateinit var specsPane: HBox
    @FXML private lateinit var requiredLevelLabel: Label
    @FXML private lateinit var remainingPointsLabel: Label

    @FXML
    private fun initialize() {
        val loader = FXMLLoader(javaClass.getResource("$ASSETS_ROOT/views/SpecializationView.fxml"))
        for (spec in wowClass.specializations) {
            val controller = SpecializationController().also { it.specialization = spec }
            loader.setController(controller)
            specsPane.children += loader.load<Region>()
        }

        requiredLevelLabel.text = "Required Level: 10"
        remainingPointsLabel.text = "Remaining Points: 51"
    }
}