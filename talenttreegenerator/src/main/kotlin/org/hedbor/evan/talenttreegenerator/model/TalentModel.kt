package org.hedbor.evan.talenttreegenerator.model

import tornadofx.ItemViewModel


class TalentModel(initialValue: Talent? = null) : ItemViewModel<Talent>(initialValue) {
    val displayName = bind(Talent::displayNameProperty)
    val translationKey = bind(Talent::translationKeyProperty)
    val description = bind(Talent::descriptionProperty)
    private val locationProperty = bind(Talent::locationProperty)
    val location = LocationModel()
    val maxRank = bind(Talent::maxRankProperty)
    val icon = bind(Talent::iconProperty)
    val hasPrerequisite = bind(Talent::hasPrerequisiteProperty)
    private val prerequisiteProperty = bind(Talent::prerequisiteProperty)
    val prerequisite = LocationModel()
    val isSpell = bind(Talent::isSpellProperty)
    private val spellProperty = bind(Talent::spellProperty)
    val spell = SpellModel()

    init {
        location.itemProperty.bind(locationProperty)
        prerequisite.itemProperty.bind(prerequisiteProperty)
        spell.itemProperty.bind(spellProperty)
    }
}