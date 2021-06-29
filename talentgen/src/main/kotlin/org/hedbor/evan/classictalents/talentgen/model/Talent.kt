package org.hedbor.evan.classictalents.talentgen.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.Serializable
import org.hedbor.evan.talenttreegenerator.model.serializers.TalentSerializer
import tornadofx.getValue
import tornadofx.setValue

@Suppress("unused", "HasPlatformType")
@Serializable(with = TalentSerializer::class)
class Talent(
    displayName: String? = null,
    translationKey: String? = null,
    location: Location = Location(),
    description: String? = null,
    maxRank: Int = 0,
    icon: String? = null,
    hasPrerequisite: Boolean = false,
    prerequisite: Location = Location(),
    isSpell: Boolean = false,
    spell: Spell = Spell(),
    validated: Boolean = false
) {
    companion object {
        const val HELPFUL_DESCRIPTION = "{0,choice,1#ONE_POINT|2#TWO_POINTS|3#THREE_POINTS|4#FOUR_POINTS|5#FIVE_POINTS}"
    }

    val displayNameProperty = SimpleStringProperty(this, "displayName", displayName)
    var displayName by displayNameProperty

    val translationKeyProperty = SimpleStringProperty(this, "translationKey", translationKey)
    var translationKey by translationKeyProperty

    val locationProperty = SimpleObjectProperty<Location>(this, "location", location)
    var location by locationProperty

    val descriptionProperty = SimpleStringProperty(this, "description", description)
    var description by descriptionProperty

    val maxRankProperty = SimpleIntegerProperty(this, "maxRank", maxRank)
    var maxRank by maxRankProperty

    val iconProperty = SimpleStringProperty(this, "icon", icon)
    var icon by iconProperty

    val hasPrerequisiteProperty = SimpleBooleanProperty(this, "hasPrerequisite", hasPrerequisite)
    var hasPrerequisite by hasPrerequisiteProperty

    val prerequisiteProperty = SimpleObjectProperty<Location>(this, "prerequisite", prerequisite)
    var prerequisite by prerequisiteProperty

    val isSpellProperty = SimpleBooleanProperty(this, "isSpell", isSpell)
    var isSpell by isSpellProperty

    val spellProperty = SimpleObjectProperty<Spell>(this, "spell", spell)
    var spell by spellProperty

    val validatedProperty = SimpleBooleanProperty(this, "isValid", validated)
    var validated by validatedProperty

    override fun toString(): String {
        return """Talent(
            |displayName='$displayName', translationKey='$translationKey', location=$location, 
            |description='$description', maxRank=$maxRank, icon='$icon', hasPrerequisite=$hasPrerequisite, 
            |prerequisite=$prerequisite, isSpell=$isSpell, spell=$spell, validated=$validated)""".trimMargin()
    }
}