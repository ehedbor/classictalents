package org.hedbor.evan.classictalents.control

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Region
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.util.getProperty
import org.hedbor.evan.classictalents.util.property

class ClassButton : Button() {
    var icon by property<Image?>()
    fun iconProperty() = getProperty(ClassButton::icon)

    var isSelected by property(false)
    fun selectedProperty() = getProperty(ClassButton::isSelected)

    @FXML private lateinit var iconView: ImageView
    @FXML private lateinit var highlightView: ImageView
    @FXML private lateinit var activeBorderRegion: Region

    init {
        val loader = FXMLLoader(javaClass.getResource("$ASSETS_ROOT/view/ClassButtonView.fxml"))
        loader.setRoot(this)
        loader.setController(this)
        loader.load<Button>()
    }

    @FXML
    private fun initialize() {
        iconView.imageProperty().bind(iconProperty())
        highlightView.visibleProperty().bind(hoverProperty())
        selectedProperty().addListener { _, _, selected ->
            if (selected) {
                activeBorderRegion.styleClass.add("active-border")
            } else {
                activeBorderRegion.styleClass.clear()
            }
        }
    }
}