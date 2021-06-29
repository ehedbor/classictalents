package org.hedbor.evan.classictalents.talentgen.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import kotlinx.serialization.Serializable
import org.hedbor.evan.talenttreegenerator.model.serializers.WowClassSerializer
import tornadofx.getValue
import tornadofx.observableListOf
import tornadofx.setValue


@Suppress("unused", "HasPlatformType")
@Serializable(with = WowClassSerializer::class)
class WowClass(
    displayName: String? = null,
    translationKey: String? = null,
    specializations: ObservableList<Specialization> = observableListOf()
) {
    val displayNameProperty = SimpleStringProperty(this, "displayName", displayName)
    var displayName by displayNameProperty

    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey by translationKeyProperty

    val specializationsProperty = SimpleListProperty<Specialization>(this, "specializations", specializations)
    var specializations by specializationsProperty

    override fun toString(): String {
        return "WowClass(displayName='$displayName', translationKey='$translationKey', specializations=$specializations)"
    }
}