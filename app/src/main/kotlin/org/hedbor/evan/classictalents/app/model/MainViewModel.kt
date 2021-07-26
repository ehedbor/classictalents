package org.hedbor.evan.classictalents.app.model

import javafx.application.Platform
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import javafx.util.Duration
import org.hedbor.evan.classictalents.app.service.FileService
import org.hedbor.evan.classictalents.app.service.TooltipService
import org.hedbor.evan.classictalents.common.model.WowClass
import tornadofx.*
import kotlin.system.exitProcess


class MainViewModel : ViewModel() {
    val classesProperty = SimpleListProperty(observableListOf<WowClass>())
    var classes: ObservableList<WowClass> by classesProperty

    init {
        rebindOnChange(classesProperty)
    }

    fun onSetup() {
        TooltipService.setGlobalTooltipBehavior(Duration.ZERO, Duration.INDEFINITE, Duration.ZERO)
        classes = FileService.loadClasses("/talents/warlock.json").toObservable()
        // this causes a crash if a key is not found
        FX.messages = FileService.loadBundles("AllMessages", "bundles.Messages", "bundles.warlock")
    }

    fun onExit() {
        Platform.exit()
        exitProcess(0)
    }
}