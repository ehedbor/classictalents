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

package org.hedbor.evan.classictalents.app.view

import org.hedbor.evan.classictalents.app.model.MainViewModel
import org.hedbor.evan.classictalents.app.model.SpecializationViewModel
import tornadofx.*

class MainView : View("Classic WoW Talent Calculator") {
    private val model by inject<MainViewModel>()

    init {
        model.onSetup()
    }

    override val root = hbox {
        val wowClass = model.classes.first()
        for (spec in wowClass.specializations) {
            val scope = Scope(SpecializationViewModel(wowClass, spec))
            this += find<SpecializationFragment>(scope)
        }
    }
}
