package org.hedbor.evan.classictalents.talentgen.model

import org.hedbor.evan.classictalents.common.model.Specialization
import tornadofx.ItemViewModel


class SpecializationModel(initialValue: Specialization? = null) : ItemViewModel<Specialization>(initialValue) {
    val translationKey = bind(Specialization::translationKeyProperty)
    val backgroundImage = bind(Specialization::backgroundImageProperty)
    val talents = bind(Specialization::talentsProperty)
}