package org.hedbor.evan.talenttreegenerator.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.getValue
import tornadofx.setValue

class Talent(
    displayName: String? = null,
    translationKey: String? = null,
    location: Location? = null,
    description: String? = null,
    maxRank: Int = Integer.MIN_VALUE,
    icon: String? = null,
    hasPrerequisite: Boolean = false,
    prerequisite: Location? = null,
    isSpell: Boolean = false,
    spellInfo: Spell? = null,
) {
    val displayNameProperty = SimpleStringProperty(this, "displayName", displayName)
    var displayName by displayNameProperty

    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey by translationKeyProperty

    val locationProperty = SimpleObjectProperty(this, "location", location)
    var location by locationProperty

    val descriptionProperty = SimpleStringProperty(this, "description", description)
    var description by descriptionProperty

    val maxRankProperty = SimpleIntegerProperty(this, "maxRank", maxRank)
    var maxRank by maxRankProperty

    val iconProperty = SimpleStringProperty(this, "icon", icon)
    var icon by iconProperty

    val hasPrerequisiteProperty = SimpleBooleanProperty(this, "hasPrerequisite", hasPrerequisite)
    val hasPrerequisite by hasPrerequisiteProperty

    val prerequisiteProperty = SimpleObjectProperty(this, "prerequisite", prerequisite)
    val prerequisite by prerequisiteProperty

    val isSpellProperty = SimpleBooleanProperty(this, "isSpell", isSpell)
    var isSpell by isSpellProperty

    val spellInfoProperty = SimpleObjectProperty(this, "spellInfo", spellInfo)
    var spellInfo by spellInfoProperty
}