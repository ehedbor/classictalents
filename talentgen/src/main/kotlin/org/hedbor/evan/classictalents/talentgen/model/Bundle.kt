/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2022 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.talentgen.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import tornadofx.getValue
import tornadofx.observableListOf
import tornadofx.setValue
import java.util.*


class Bundle(
    locale: Locale = Locale.ROOT,
    entries: ObservableList<BundleEntry> = observableListOf()
) {
    val localeProperty = SimpleObjectProperty(this, "locale", locale)
    var locale: Locale by localeProperty
    
    val entriesProperty = SimpleListProperty(this, "entries", entries)
    var entries: ObservableList<BundleEntry> by entriesProperty
}

class BundleEntry(translationKey: String = "", displayName: String = "") {
    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey: String by translationKeyProperty

    val displayNameProperty = SimpleStringProperty(this, "displayName", displayName)
    var displayName: String by displayNameProperty

    operator fun component1() = translationKey
    operator fun component2() = displayName
}