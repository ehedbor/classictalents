package org.hedbor.evan.classictalents.common.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import kotlinx.serialization.Serializable
import org.hedbor.evan.classictalents.common.serialization.WowClassSerializer
import tornadofx.getValue
import tornadofx.observableListOf
import tornadofx.setValue


@Suppress("MemberVisibilityCanBePrivate")
@Serializable(with = WowClassSerializer::class)
class WowClass(
    translationKey: String = "",
    era: Era = Era.CLASSIC,
    specializations: ObservableList<Specialization> = observableListOf()
) {
    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey: String by translationKeyProperty

    val eraProperty = SimpleObjectProperty<Era>(this, "era", era)
    var era: Era by eraProperty

    val specializationsProperty: SimpleListProperty<Specialization> = SimpleListProperty(this, "specializations", specializations)
    var specializations: ObservableList<Specialization> by specializationsProperty

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WowClass) return false

        if (translationKey != other.translationKey) return false
        if (era != other.era) return false
        if (specializations != other.specializations) return false

        return true
    }

    override fun hashCode(): Int {
        var result = translationKey.hashCode()
        result = 31 * result + era.hashCode()
        result = 31 * result + specializations.hashCode()
        return result
    }

    override fun toString(): String {
        return "WowClass(translationKey='$translationKey', era=$era, specializations=$specializations)"
    }
}

