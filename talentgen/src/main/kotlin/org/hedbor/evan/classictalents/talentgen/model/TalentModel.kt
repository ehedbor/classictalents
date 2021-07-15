package org.hedbor.evan.classictalents.talentgen.model

import org.hedbor.evan.classictalents.common.model.Talent
import tornadofx.ItemViewModel


class TalentModel(initialValue: Talent? = null) : ItemViewModel<Talent>(initialValue) {
    val translationKey = bind(Talent::translationKeyProperty)
    val location = bind(Talent::locationProperty)
    val prerequisite = bind(Talent::prerequisiteProperty)
    val maxRank = bind(Talent::maxRankProperty)
    val rank = bind(Talent::rankProperty)
    val icon = bind(Talent::iconProperty)
    val spell = bind(Talent::spellProperty)
}