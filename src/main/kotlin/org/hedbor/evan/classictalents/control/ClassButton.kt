package org.hedbor.evan.classictalents.control

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Region
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.util.delegate

class ClassButton : Button() {
    // TODO: default icon
    private val _icon = SimpleObjectProperty<Image>()
    var icon: Image by _icon.delegate()
    fun iconProperty() = _icon

    private val _isSelected = SimpleBooleanProperty(false)
    var isSelected by _isSelected.delegate()
    fun selectedProperty() = _isSelected

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
        hoverProperty().addListener { _, _, isHover ->
            if (isHover) {
                val bounds = localToScreen(boundsInLocal)
                tooltip.show(this, bounds.maxX, bounds.minY)
            } else {
                tooltip.hide()
            }
        }
    }
}