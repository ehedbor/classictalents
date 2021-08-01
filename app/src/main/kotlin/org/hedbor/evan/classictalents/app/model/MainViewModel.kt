/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2021 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.app.model

import javafx.application.Platform
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
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

    val activeClassKey = SimpleStringProperty("warlock")

    init {
        rebindOnChange(classesProperty)
    }

    fun onSetup() {
        TooltipService.setGlobalTooltipBehavior(Duration.ZERO, Duration.INDEFINITE, Duration.ZERO)
        classes = FileService.loadClasses("/talents/warlock.json", "/talents/test_class.json").toObservable()
        // this causes a crash if a key is not found
        FX.messages = FileService.loadBundles("AllMessages", "bundles.Messages", "bundles.warlock", "bundles.test_class")
    }

    fun onExit() {
        Platform.exit()
        exitProcess(0)
    }

    fun onClassButtonClicked(wowClassKey: ObservableValue<String>) {
        activeClassKey.unbind()
        activeClassKey.bind(wowClassKey)
    }
}