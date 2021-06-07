package org.hedbor.evan.talenttreegenerator.model

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.getValue
import tornadofx.setValue


class WowClass(
    displayName: String? = null,
    translationKey: String? = null,
    spec1: Specialization? = null,
    spec2: Specialization? = null,
    spec3: Specialization? = null
) {
    val displayNameProperty = SimpleStringProperty(this, "displayName", displayName)
    var displayName by displayNameProperty

    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey by translationKeyProperty

    val spec1Property = SimpleObjectProperty(this, "spec1", spec1)
    var spec1 by spec1Property

    val spec2Property = SimpleObjectProperty(this, "spec2", spec2)
    var spec2 by spec2Property

    val spec3Property = SimpleObjectProperty(this, "spec3", spec3)
    var spec3 by spec3Property
}