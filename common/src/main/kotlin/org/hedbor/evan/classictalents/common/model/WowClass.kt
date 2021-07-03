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
import kotlin.Int
import kotlin.String
import kotlin.Suppress


@Suppress("MemberVisibilityCanBePrivate")
@Serializable(with = WowClassSerializer::class)
class WowClass(
    displayName: String = "",
    translationKey: String = "",
    era: Era? = null,
    specializations: ObservableList<Specialization> = observableListOf()
) {
    val displayNameProperty = SimpleStringProperty(this, "displayName", displayName)
    var displayName: String by displayNameProperty

    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey: String by translationKeyProperty

    val eraProperty = SimpleObjectProperty<Era>(this, "era", era)
    var era: Era by eraProperty

    val specializationsProperty: SimpleListProperty<Specialization> =
        SimpleListProperty(this, "specializations", specializations)
    var specializations: ObservableList<Specialization> by specializationsProperty
}

/** @see Specialization.TALENT_COLUMN_COUNT */
enum class Era(val key: String, val talentRowCount: Int) {
    VANILLA("vanilla", 7),
    TBC("tbc", 9),
    WOTLK("wotlk", 11);

    override fun toString() = key
}