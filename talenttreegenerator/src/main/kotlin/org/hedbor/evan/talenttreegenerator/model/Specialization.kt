package org.hedbor.evan.talenttreegenerator.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import tornadofx.getValue
import tornadofx.observableListOf
import tornadofx.setValue

@Suppress("HasPlatformType", "unused")
class Specialization(displayName: String? = null, translationKey: String? = null, backgroundImage: String? = null, talents: ObservableList<Talent> = observableListOf()) {
    companion object {
        internal const val ROWS = 7
        internal const val COLUMNS = 4
    }

    val displayNameProperty = SimpleStringProperty(this, "displayName", displayName)
    var displayName by displayNameProperty

    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey by translationKeyProperty

    val backgroundImageProperty = SimpleStringProperty(this, "backgroundImage", backgroundImage)
    var backgroundImage by backgroundImageProperty

    val talentsProperty = SimpleListProperty(this, "talents", talents)
    var talents by talentsProperty
}