package org.hedbor.evan.classictalents.common.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import kotlinx.serialization.Serializable
import org.hedbor.evan.classictalents.common.serialization.SpecializationSerializer
import tornadofx.getValue
import tornadofx.observableListOf
import tornadofx.setValue
import kotlin.String
import kotlin.Suppress


@Suppress("MemberVisibilityCanBePrivate")
@Serializable(with = SpecializationSerializer::class)
class Specialization(
    displayName: String = "",
    translationKey: String? = "",
    backgroundImage: String? = "",
    talents: ObservableList<Talent> = observableListOf(),
) {
    companion object {
        /** @see Era.talentRowCount */
        const val TALENT_COLUMN_COUNT = 4
    }

    val displayNameProperty = SimpleStringProperty(this, "displayName", displayName)
    var displayName: String by displayNameProperty

    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey: String by translationKeyProperty

    val backgroundImageProperty = SimpleStringProperty(this, "backgroundImage", backgroundImage)
    var backgroundImage: String by backgroundImageProperty

    val talentsProperty = SimpleListProperty(this, "talents", talents)
    var talents: ObservableList<Talent> by talentsProperty
}