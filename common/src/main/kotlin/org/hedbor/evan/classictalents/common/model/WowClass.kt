package org.hedbor.evan.classictalents.common.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import kotlinx.serialization.Serializable
import org.hedbor.evan.classictalents.common.serialization.WowClassSerializer
import tornadofx.*


@Suppress("MemberVisibilityCanBePrivate")
@Serializable(with = WowClassSerializer::class)
class WowClass(
    displayName: String = "",
    translationKey: String = "",
    specializations: ObservableList<Specialization> = observableListOf()
) {
    val displayNameProperty = SimpleStringProperty(this, "displayName", displayName)
    var displayName: String by displayNameProperty

    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey: String by translationKeyProperty

    val specializationsProperty: SimpleListProperty<Specialization> = SimpleListProperty(this, "specializations", specializations)
    var specializations: ObservableList<Specialization> by specializationsProperty
}