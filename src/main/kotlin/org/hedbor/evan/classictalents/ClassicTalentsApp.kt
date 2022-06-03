package org.hedbor.evan.classictalents

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage


class ClassicTalentsApp : Application() {
    override fun start(primaryStage: Stage) {
        val page = FXMLLoader.load<VBox>(javaClass.getResource("/org/hedbor/evan/classictalents/view/MainView.fxml"))
        val scene = Scene(page)

        primaryStage.title = "title goes here"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(ClassicTalentsApp::class.java, *args)
}