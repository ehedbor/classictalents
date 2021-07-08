package org.hedbor.evan.classictalents.common.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.hedbor.evan.classictalents.common.serialization.SimpleIntegerPropertySerializer
import org.hedbor.evan.classictalents.common.serialization.SimpleObjectPropertySerializer
import org.hedbor.evan.classictalents.common.serialization.SimpleStringPropertySerializer
import tornadofx.getValue
import tornadofx.setValue
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit


@Suppress("MemberVisibilityCanBePrivate")
@Serializable
class Talent {
    companion object {
        const val MINIMUM_RANK = 1
        const val MAXIMUM_PERMISSIBLE_RANK = 5
    }

    constructor(block: Talent.() -> Unit) { this.block() }

    @Serializable(with = SimpleStringPropertySerializer::class)
    @SerialName("key")
    val translationKeyProperty = SimpleStringProperty(this, "translationKey", "")
    var translationKey: String by translationKeyProperty

    @Serializable(with = SimpleObjectPropertySerializer::class)
    @SerialName("location")
    val locationProperty = SimpleObjectProperty(this, "location", Location())
    var location: Location by locationProperty

    @Serializable(with = SimpleObjectPropertySerializer::class)
    @SerialName("requires")
    val prerequisiteProperty = SimpleObjectProperty<Location>(this, "prerequisite", null)
    var prerequisite: Location? by prerequisiteProperty

    @Serializable(with = SimpleIntegerPropertySerializer::class)
    @SerialName("maxRank")
    val maxRankProperty = SimpleIntegerProperty(this, "maxRank", 0)
    var maxRank: Int by maxRankProperty

    @Transient
    val rankProperty = SimpleIntegerProperty(this, "rank", 0)
    var rank: Int by rankProperty

    @Serializable(with = SimpleStringPropertySerializer::class)
    @SerialName("icon")
    val iconProperty = SimpleStringProperty(this, "icon", "")
    var icon: String by iconProperty

    @Serializable(with = SimpleObjectPropertySerializer::class)
    @SerialName("spell")
    val spellProperty = SimpleObjectProperty<Spell>(this, "spell", null)
    var spell: Spell? by spellProperty
}