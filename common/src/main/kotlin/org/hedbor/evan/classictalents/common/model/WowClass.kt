package org.hedbor.evan.classictalents.common.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hedbor.evan.classictalents.common.serialization.SimpleListPropertySerializer
import org.hedbor.evan.classictalents.common.serialization.SimpleObjectPropertySerializer
import org.hedbor.evan.classictalents.common.serialization.SimpleStringPropertySerializer
import tornadofx.getValue
import tornadofx.observableListOf
import tornadofx.setValue


@Suppress("MemberVisibilityCanBePrivate")
@Serializable
class WowClass() {
    constructor(block: WowClass.() -> Unit) : this() { this.block() }

    @Serializable(with = SimpleStringPropertySerializer::class)
    @SerialName("key")
    val translationKeyProperty = SimpleStringProperty(this, "translationKey", "")
    var translationKey: String by translationKeyProperty

    @Serializable(with = SimpleObjectPropertySerializer::class)
    @SerialName("era")
    val eraProperty = SimpleObjectProperty<Era>(this, "era", Era.CLASSIC)
    var era: Era by eraProperty

    @Serializable(with = SimpleListPropertySerializer::class)
    @SerialName("specs")
    val specializationsProperty: SimpleListProperty<Specialization> = SimpleListProperty(this, "specializations", observableListOf())
    var specializations: ObservableList<Specialization> by specializationsProperty
}

