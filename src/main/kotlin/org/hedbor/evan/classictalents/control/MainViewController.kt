package org.hedbor.evan.classictalents.control

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.layout.HBox
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.model.MainModel
import org.hedbor.evan.classictalents.util.booleanBinding

class MainViewController {
    // TODO: do dependency injection
    private val model = MainModel().also { it.loadClasses() }

    @FXML private lateinit var classButtonsPane: HBox
    @FXML private lateinit var classViewPane: HBox

    @FXML
    private fun initialize() {
        for (wowClass in model.classes) {
            val button = ClassButton()
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