package org.hedbor.evan.classictalents.common.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hedbor.evan.classictalents.common.serialization.SimpleListPropertySerializer
import org.hedbor.evan.classictalents.common.serialization.SimpleStringPropertySerializer
import tornadofx.getValue
import tornadofx.observableListOf
import tornadofx.setValue


@Serializable
class Specialization() {
    companion object {
        /** @see Era.talentRowCount */
        const val TALENT_COLUMN_COUNT = 4
    }

    constructor(block: Specialization.() -> Unit) : this() { this.block() }

    @Serializable(with = SimpleStringPropertySerializer::class)
    @SerialName("key")
    val translationKeyProperty = SimpleStringProperty(this, "translationKey", "")
    var translationKey: String by translationKeyProperty

    @Serializable(with = SimpleStringPropertySerializer::class)
    @SerialName("backgroundImage")
    val backgroundImageProperty = SimpleStringProperty(this, "backgroundImage", "")
    var backgroundImage: String by backgroundImageProperty

    @Serializable(with = SimpleListPropertySerializer::class)
    @SerialName("talents")
    val talentsProperty = SimpleListProperty(this, "talents", observableListOf<Talent>())
    var talents: ObservableList<Talent> by talentsProperty
}
