package org.hedbor.evan.classictalents.common.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import kotlinx.serialization.Serializable
import org.hedbor.evan.classictalents.common.serialization.SpecializationSerializer
import tornadofx.getValue
import tornadofx.observableListOf
import tornadofx.setValue


@Serializable(with = SpecializationSerializer::class)
class Specialization(
    translationKey: String = "",
    backgroundImage: String = "",
    talents: ObservableList<Talent> = observableListOf()
) {
    companion object {
        /** @see Era.talentRowCount */
        const val TALENT_COLUMN_COUNT = 4
    }

    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey: String by translationKeyProperty

    val backgroundImageProperty = SimpleStringProperty(this, "backgroundImage", backgroundImage)
    var backgroundImage: String by backgroundImageProperty

    val talentsProperty = SimpleListProperty(this, "talents", talents)
    var talents: ObservableList<Talent> by talentsProperty

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Specialization) return false

        if (translationKey != other.translationKey) return false
        if (backgroundImage != other.backgroundImage) return false
        if (talents != other.talents) return false

        return true
    }

    override fun hashCode(): Int {
        var result = translationKey.hashCode()
        result = 31 * result + backgroundImage.hashCode()
        result = 31 * result + talents.hashCode()
        return result
    }

    override fun toString(): String {
        return "Specialization(translationKey='$translationKey', backgroundImage='$backgroundImage', talents=$talents)"
    }
}
