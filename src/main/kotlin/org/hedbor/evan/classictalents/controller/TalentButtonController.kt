package org.hedbor.evan.classictalents.controller

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import org.hedbor.evan.classictalents.model.Talent

class TalentButtonController {
    lateinit var talent: Talent

    @FXML private lateinit var talentButton: Button
    @FXML private lateinit var talentButtonTooltip: Tooltip
    @FXML private lateinit var iconView: ImageView
    @FXML private lateinit var highlightView: ImageView
    @FXML private lateinit var activeBorderRegion: Region
    @FXML private lateinit var rankCounterLabel: Label

    @FXML
    private fun initialize() {
        talentButtonTooltip.text = "Test"
        highlightView.visibleProperty().bind(talentButton.hoverProperty())
        activeBorderRegion.styleClass.clear()
        rankCounterLabel.text = "1"
    }

    @FXML
    private fun onMouseClicked(event: MouseEvent) {
        TODO()
    }
}