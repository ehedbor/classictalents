package org.hedbor.evan.talenttreegenerator.model

import tornadofx.ItemViewModel


class TalentModel : ItemViewModel<Talent>() {
    val displayName = bind(Talent::displayNameProperty)
    val translationKey = bind(Talent::translationKeyProperty)
    val description = bind(Talent::descriptionProperty)
    val location = LocationModel()
    val maxRank = bind(Talent::maxRankProperty)
    val icon = bind(Talent::iconProperty)
    val hasPrerequisite = bind(Talent::hasPrerequisiteProperty)
    val prerequisite = LocationModel()
    val isSpell = bind(Talent::isSpellProperty)
    val spellInfo = SpellModel()


    init {
        //location.itemProperty.bind(item.locationProperty)
        //prerequisite.itemProperty.bind(item.prerequisiteProperty)
        //spellInfo.itemProperty.bind(item.spellInfoProperty)
    }
}