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