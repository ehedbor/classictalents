package org.hedbor.evan.classictalents.control

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import org.hedbor.evan.classictalents.model.WowClass
import org.hedbor.evan.classictalents.util.stringBinding
import kotlin.math.round

class WowClassView(private val model: WowClass) {
    @FXML private lateinit var specsPane: HBox
    @FXML private lateinit var iconView: ImageView
    @FXML private lateinit var classNameLabel: Label
    @FXML private lateinit var spec1PtsLabel: Label
    @FXML private lateinit var spec2PtsLabel: Label
    @FXML private lateinit var spec3PtsLabel: Label
    @FXML private lateinit var requiredLevelLabel: Label
    @FXML private lateinit var remainingPointsLabel: Label

    @FXML
    private fun initialize() {
        // TODO: reuse view for different models. see MainViewController for reasoning
        setClass(model)
    }

    private fun setClass(wowClass: WowClass) {
        wowClass.specializations
            .map { SpecializationView(it) }
            .toCollection(specsPane.children)

        iconView.imageProperty().bind(wowClass.iconProperty())

        classNameLabel.textProperty().bind(
            wowClass.nameProperty().stringBinding { "$it:" })
        classNameLabel.styleProperty().bind(wowClass.colorProperty().stringBinding {
            val r = round(it.red * 255).toInt()
            val g = round(it.green * 255).toInt()
            val b = round(it.blue * 255).toInt()
            "-fx-text-fill: rgb($r, $g, $b);"
        })

        // TODO rebind if specs change
        spec1PtsLabel.textProperty().bind(wowClass.specializations[0].allocatedPointsProperty().asString())
        spec2PtsLabel.textProperty().bind(wowClass.specializations[1].allocatedPointsProperty().asString())
        spec3PtsLabel.textProperty().bind(wowClass.specializations[2].allocatedPointsProperty().asString())

        requiredLevelLabel.textProperty().bind(
            wowClass.allocatedPointsProperty().stringBinding { allocated ->
                if (allocated as Int > 0)
                    (allocated + 9).toString()
                else
                    "\u2013" // en dash
            })

        remainingPointsLabel.textProperty().bind(wowClass.maxPointsProperty().subtract(wowClass.allocatedPointsProperty()).asString())
    }
}