package org.hedbor.evan.classictalents

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

internal const val ASSETS_ROOT = "/org/hedbor/evan/classictalents"


class ClassicTalentsApp : Application() {
    override fun start(primaryStage: Stage) {
        val root = FXMLLoader.load<VBox>(
            javaClass.getResource("$ASSETS_ROOT/view/MainView.fxml"))
        val scene = Scene(root)

        val stylesheets = arrayOf(
            "$ASSETS_ROOT/styles/ClassStyles.css",
            "$ASSETS_ROOT/styles/SpecStyles.css",
            "$ASSETS_ROOT/styles/TalentStyles.css",
            "$ASSETS_ROOT/styles/TalentTooltipStyles.css",
        )
        stylesheets
            .map { javaClass.getResource(it)?.toExternalForm()
                ?: throw IllegalStateException("Could not load stylesheet '$it'") }
            .forEach { scene.stylesheets.add(it) }

        primaryStage.title = "WoW Classic Talent Calculator"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(ClassicTalentsApp::class.java, *args)
}