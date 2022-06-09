package org.hedbor.evan.classictalents.controller

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.model.WowClass
import org.hedbor.evan.classictalents.util.observableListOf

class MainController  {
    private val classes = observableListOf<WowClass>()
    @FXML private lateinit var classButtonsPane: HBox
    @FXML private lateinit var classViewPane: HBox

    @FXML
    private fun initialize() {
        val talentLoader = FXMLLoader(javaClass.getResource("$ASSETS_ROOT/views/TalentButtonView.fxml"))
        val classLoader = FXMLLoader(javaClass.getResource("$ASSETS_ROOT/views/WowClassView.fxml"))
        for (wowClass in classes) {
            // TODO: create another controller to handle the class selection button
            val buttonController = TalentButtonController()
            talentLoader.setController(buttonController)
            classButtonsPane.children += talentLoader.load<Region>()

            val classController = WowClassController().also { it.wowClass = wowClass }
            classLoader.setController(classController)
            classViewPane.children += classLoader.load<Region>()
            // TODO: rebind depending on selected class
        }
    }
}