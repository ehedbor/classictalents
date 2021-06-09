package org.hedbor.evan.talenttreegenerator.model

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.getValue
import tornadofx.setValue


@Suppress("unused", "HasPlatformType")
class WowClass(
    displayName: String? = null,
    translationKey: String? = null,
    spec1: Specialization = Specialization(),
    spec2: Specialization = Specialization(),
    spec3: Specialization = Specialization()
) {
    val displayNameProperty = SimpleStringProperty(this, "displayName", displayName)
    var displayName by displayNameProperty

    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey by translationKeyProperty

    val spec1Property = SimpleObjectProperty<Specialization>(this, "spec1", spec1)
    var spec1 by spec1Property

    val spec2Property = SimpleObjectProperty<Specialization>(this, "spec2", spec2)
    var spec2 by spec2Property

    val spec3Property = SimpleObjectProperty<Specialization>(this, "spec3", spec3)
    var spec3 by spec3Property
}