package org.hedbor.evan.classictalents.common.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.Serializable
import org.hedbor.evan.classictalents.common.serialization.TalentSerializer
import tornadofx.getValue
import tornadofx.setValue
import kotlin.Int
import kotlin.String
import kotlin.Suppress


@Suppress("MemberVisibilityCanBePrivate")
@Serializable(with = TalentSerializer::class)
class Talent(
    translationKey: String = "",
    displayName: String = "",
    description: String = "",
    location: Location = Location(),
    prerequisite: Location? = null,
    maxRank: Int = 0,
    icon: String = "",
    spell: Spell? = null,
) {
    companion object {
        const val MINIMUM_RANK = 1
        const val MAXIMUM_PERMISSIBLE_RANK = 5
        const val HELPFUL_DESCRIPTION = "{0,choice,1#ONE_POINT|2#TWO_POINTS|3#THREE_POINTS|4#FOUR_POINTS|5#FIVE_POINTS}"
    }

    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey: String by translationKeyProperty

    val displayNameProperty = SimpleStringProperty(this, "displayName", displayName)
    var displayName: String by displayNameProperty

    val descriptionProperty = SimpleStringProperty(this, "description", description)
    var description: String by descriptionProperty

    val locationProperty = SimpleObjectProperty(this, "location", location)
    var location: Location by locationProperty

    val prerequisiteProperty = SimpleObjectProperty<Location>(this, "prerequisite", prerequisite)
    var prerequisite: Location? by prerequisiteProperty

    val maxRankProperty = SimpleIntegerProperty(this, "maxRank", maxRank)
    var maxRank: Int by maxRankProperty

    val iconProperty = SimpleStringProperty(this, "icon", icon)
    var icon: String by iconProperty

    val spellProperty = SimpleObjectProperty<Spell>(this, "spell", spell)
    var spell: Spell? by spellProperty
}