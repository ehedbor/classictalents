package org.hedbor.evan.classictalents

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage


internal const val ASSETS_ROOT = "/org/hedbor/evan/classictalents"

class ClassicTalentsApp : Application() {
    override fun start(primaryStage: Stage) {
        val view = FXMLLoader.load<VBox>(javaClass.getResource("$ASSETS_ROOT/view/MainView.fxml"))
        val scene = Scene(view, 1300.0, 1000.0)

        val frizQuadrataTT = javaClass.getResource("$ASSETS_ROOT/fonts/FrizQuadrataTT.ttf")?.toExternalForm()
        Font.loadFont(frizQuadrataTT, 12.0)

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

        primaryStage.title = "WoW Classic Talent Calculator"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(ClassicTalentsApp::class.java, *args)
}