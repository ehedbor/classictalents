package org.hedbor.evan.classictalents.talentgen.model

import tornadofx.ItemViewModel


class SpecializationModel(initialValue: Specialization? = null) : ItemViewModel<Specialization>(initialValue) {
    val displayName = bind(Specialization::displayNameProperty)
    val translationKey = bind(Specialization::translationKeyProperty)
    val backgroundImage = bind(Specialization::backgroundImageProperty)
    val talents = bind(Specialization::talentsProperty)
    val validated = bind(Specialization::validatedProperty)
}