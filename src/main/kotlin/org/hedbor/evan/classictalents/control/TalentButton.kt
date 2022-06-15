package org.hedbor.evan.classictalents.control

import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.model.Talent
import org.hedbor.evan.classictalents.util.getProperty
import org.hedbor.evan.classictalents.util.objectBinding
import org.hedbor.evan.classictalents.util.property
import org.hedbor.evan.classictalents.util.stringBinding

class TalentButton(private val model: Talent) : StackPane() {
    @FXML private lateinit var button: Button
    // TODO: tooltip
    @FXML private lateinit var iconView: ImageView
    @FXML private lateinit var highlightView: ImageView
    @FXML private lateinit var activeBorderRegion: Region
    @FXML private lateinit var rankCounterLabel: Label

    private val rankListener = ChangeListener<Number?> { _, _, _ -> updateActiveBorder() }

    init {
        val loader = FXMLLoader(javaClass.getResource("$ASSETS_ROOT/view/TalentButtonView.fxml"))
        loader.setRoot(this)
        loader.setController(this)
        loader.load<StackPane>()
    }

    @Suppress("unused")
    @FXML
    private fun initialize() {
        highlightView.visibleProperty().bind(button.hoverProperty())
        iconView.imageProperty().bind(model.iconProperty())
        rankCounterLabel.textProperty().bind(
            model.rankProperty().stringBinding {
                it!!.toString()
            })

        model.rankProperty().addListener(rankListener)
        model.maxRankProperty().addListener(rankListener)

    }

    private fun updateActiveBorder() {
        activeBorderRegion.styleClass.clear()
        if (model.rank >= model.maxRank) {
            activeBorderRegion.styleClass += "maxed-out-border"
        } else if (model.rank > 0) {
            activeBorderRegion.styleClass += "active-border"
        } else {
            // do nothing, style already cleared
        }
    }

    @Suppress("unused")
    @FXML
    private fun onMouseClicked(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            if (model.rank < model.maxRank) {
                model.rank++
            }
        } else if (event.button == MouseButton.SECONDARY) {
            if (model.rank > 0) {
                model.rank--
            }
        }
    }
}