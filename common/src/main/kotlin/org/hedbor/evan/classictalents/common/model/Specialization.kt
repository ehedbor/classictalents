package org.hedbor.evan.classictalents.common.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import kotlinx.serialization.Serializable
import org.hedbor.evan.classictalents.common.serialization.SpecializationSerializer
import tornadofx.*


@Suppress("MemberVisibilityCanBePrivate")
@Serializable(with = SpecializationSerializer::class)
class Specialization(
    displayName: String = "",
    translationKey: String? = "",
    backgroundImage: String? = "",
    talents: ObservableList<Talent> = observableListOf()
) {
    val displayNameProperty = SimpleStringProperty(this, "displayName", displayName)
    var displayName: String by displayNameProperty

    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey: String by translationKeyProperty

    val backgroundImageProperty = SimpleStringProperty(this, "backgroundImage", backgroundImage)
    var backgroundImage: String by backgroundImageProperty

    val talentsProperty = SimpleListProperty(this, "talents", talents)
    var talents: ObservableList<Talent> by talentsProperty
}