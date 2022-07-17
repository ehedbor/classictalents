package org.hedbor.evan.classictalents

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import kotlin.random.Random


internal const val ASSETS_ROOT = "/org/hedbor/evan/classictalents"

class ClassicTalentsApp : Application() {
    override fun start(primaryStage: Stage) {
        primaryStage.title = "WoW Classic Talent Calculator"
        loadIcons(primaryStage)

        val view = FXMLLoader.load<VBox>(javaClass.getResource("$ASSETS_ROOT/view/MainView.fxml"))
        // TODO: determine min fit size automatically
        val scene = Scene(view, 970.0, 1110.0)

        loadFonts()
        loadStylesheets(scene)

        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun loadIcons(primaryStage: Stage) {
        // Very rarely, use that one guy's face as the icon
        if (Random.nextInt(1, 21) == 20) {
            primaryStage.icons.addAll(
                Image(javaClass.getResourceAsStream("$ASSETS_ROOT/icons/LargeIcon.png")),
                Image(javaClass.getResourceAsStream("$ASSETS_ROOT/icons/MediumIcon.png")),
                Image(javaClass.getResourceAsStream("$ASSETS_ROOT/icons/SmallIcon.png")))
        } else {
            primaryStage.icons.addAll(
                Image(javaClass.getResourceAsStream("$ASSETS_ROOT/icons/LargeIcon2.png")),
                Image(javaClass.getResourceAsStream("$ASSETS_ROOT/icons/MediumIcon2.png")),
                Image(javaClass.getResourceAsStream("$ASSETS_ROOT/icons/SmallIcon2.png")))
        }
    }

    private fun loadFonts() {
        val uiFont = javaClass.getResource("$ASSETS_ROOT/fonts/FrizQuadrataTT.ttf")?.toExternalForm()
        Font.loadFont(uiFont, 368.0)
    }

    private fun loadStylesheets(scene: Scene) {
        val stylesheets = arrayOf(
            "$ASSETS_ROOT/styles/ClassStyles.css",
            "$ASSETS_ROOT/styles/SpecStyles.css",
            "$ASSETS_ROOT/styles/TalentStyles.css",
            "$ASSETS_ROOT/styles/TalentTooltipStyles.css",
        )
        stylesheets
            .map { path ->
                javaClass.getResource(path)?.toExternalForm()
                    ?: throw IllegalStateException("Could not load stylesheet '$path'")
            }
            .forEach { scene.stylesheets.add(it) }
    }
}

fun main(args: Array<String>) {
    Application.launch(ClassicTalentsApp::class.java, *args)
}