package org.hedbor.evan.classictalents.controller

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import org.hedbor.evan.classictalents.model.WowClass

class WowClassController {
    lateinit var wowClass: WowClass

    @FXML private lateinit var specsPane: HBox
    @FXML private lateinit var requiredLevelLabel: Label
    @FXML private lateinit var remainingPointsLabel: Label

    @FXML
    private fun initialize() {
    }
}