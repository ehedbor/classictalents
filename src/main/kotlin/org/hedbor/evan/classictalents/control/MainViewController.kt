package org.hedbor.evan.classictalents.control

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.layout.HBox
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.model.MainModel
import org.hedbor.evan.classictalents.util.booleanBinding
import org.hedbor.evan.classictalents.util.stringBinding
import kotlin.math.round

class MainViewController {
    // TODO: do dependency injection
    private val model = MainModel().also { it.loadClasses() }

    @FXML private lateinit var classButtonsPane: HBox
    @FXML private lateinit var classViewPane: HBox

    @FXML
    private fun initialize() {
        for (wowClass in model.classes) {
            val button = ClassButton()
            button.tooltip.textProperty().bind(wowClass.nameProperty())
            button.tooltip.styleProperty().bind(
                wowClass.colorProperty().stringBinding {
                    val r = round(255 * it.red).toInt()
                    val g = round(255 * it.green).toInt()
                    val b = round(255 * it.blue).toInt()
                    "-fx-text-fill: rgb($r, $g, $b);"
                })
            button.iconProperty().bind(wowClass.iconProperty())
            button.selectedProperty().bind(
                model.selectedClassProperty().booleanBinding { selectedClass ->
                    wowClass == selectedClass
                })
            button.setOnAction {
                model.selectedClass = wowClass
            }

            classButtonsPane.children += button
        }

        val loader = FXMLLoader(javaClass.getResource("$ASSETS_ROOT/view/WowClassView.fxml"))
        val controller = WowClassView(model)
        loader.setController(controller)
        //loader.setRoot(controller)
        classViewPane.children += loader.load<Node>()
    }
}