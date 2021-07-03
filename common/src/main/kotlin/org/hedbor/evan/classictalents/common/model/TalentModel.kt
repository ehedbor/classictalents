package org.hedbor.evan.classictalents.common.model

import tornadofx.ItemViewModel


class TalentModel(initialValue: Talent? = null) : ItemViewModel<Talent>(initialValue) {
    val displayName = bind(Talent::displayNameProperty)
    val translationKey = bind(Talent::translationKeyProperty)
    val description = bind(Talent::descriptionProperty)
    val location = bind(Talent::locationProperty)
    val maxRank = bind(Talent::maxRankProperty)
    val icon = bind(Talent::iconProperty)
    val prerequisite = bind(Talent::prerequisiteProperty)
    val spell = bind(Talent::spellProperty)
}