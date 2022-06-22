package org.hedbor.evan.classictalents.control

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.model.Expansion
import org.hedbor.evan.classictalents.model.MainModel
import org.hedbor.evan.classictalents.model.WowClass
import org.hedbor.evan.classictalents.util.booleanBinding
import org.hedbor.evan.classictalents.util.stringBinding
import kotlin.math.round

class MainViewController {
    // TODO: do dependency injection
    private val model = MainModel().also { it.loadClasses() }

    @FXML private lateinit var expansionButtonsPane: HBox
    @FXML private lateinit var classButtonsPane: HBox
    @FXML private lateinit var classViewPane: HBox

    @FXML
    private fun initialize() {
        val group = ToggleGroup()
        // prevent de-selecting an expansion
        group.selectedToggleProperty().addListener { _, old, new ->
            if (new == null)
                old.isSelected = true
        }

        for ((expansion, classes) in model.classes) {
            expansionButtonsPane.children += makeExpansionButton(expansion, group)
            classes.forEach { addClass(it) }
        }

        classButtonsPane.visibleProperty().bind(model.selectedExpansionProperty().isNotNull)
    }

    private fun addClass(wowClass: WowClass) {
        classButtonsPane.children += makeClassButton(wowClass)

        val loader = FXMLLoader(javaClass.getResource("$ASSETS_ROOT/view/WowClassView.fxml"))
        val controller = WowClassView(wowClass)
        loader.setController(controller)

        // TODO: Recreating the specialization views is extremely slow. It would be best to reuse
        //     the same spec/talent buttons but that is also pretty difficult. To save time, keep a
        //     WowClassView in memory for each class and just turn them on and off.
        // TODO TODO: does that even affect memory usage in any significant way? might be
        //     premature optimization
        classViewPane.children += loader.load<Node>().apply {
            visibleProperty().bind(model.selectedClassProperty().isEqualTo(wowClass))
            managedProperty().bind(visibleProperty())
        }
    }

    private fun makeExpansionButton(expansion: Expansion, group: ToggleGroup) = ToggleButton().apply {
        styleClass += "expac-button"
        toggleGroup = group
        graphic = ImageView().apply {
            styleClass += "expac-button-icon"
            val imageName = when (expansion) {
                Expansion.CLASSIC -> "$ASSETS_ROOT/icons/classic.png"
                Expansion.TBC -> "$ASSETS_ROOT/icons/tbc.png"
                Expansion.WOTLK -> "$ASSETS_ROOT/icons/wotlk.png"
            }
            image = Image(MainViewController::class.java.getResourceAsStream(imageName))
        }
        setOnAction {
            model.selectedExpansion = expansion
        }
//        model.selectedExpansionProperty().addListener { _, _, new ->
//            if (new != expansion) {
//                isSelected = false
//            }
//        }
    }

    private fun makeClassButton(wowClass: WowClass) = ClassButton().apply {
        tooltip.textProperty().bind(wowClass.nameProperty())
        tooltip.styleProperty().bind(
            wowClass.colorProperty().stringBinding {
                val r = round(255 * it.red).toInt()
                val g = round(255 * it.green).toInt()
                val b = round(255 * it.blue).toInt()
                "-fx-text-fill: rgb($r, $g, $b);"
            })
        iconProperty().bind(wowClass.iconProperty())
        selectedProperty().bind(model.selectedClassProperty().isEqualTo(wowClass))
        visibleProperty().bind(model.selectedExpansionProperty().isEqualTo(wowClass.expansionProperty()))
        managedProperty().bind(visibleProperty())

        setOnAction {
            model.selectedClass = wowClass
        }
    }
}