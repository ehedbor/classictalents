package org.hedbor.evan.talenttreegenerator.model

import tornadofx.ItemViewModel


class WowClassModel : ItemViewModel<WowClass>() {
    val displayName = bind(WowClass::displayNameProperty)
    val translationKey = bind(WowClass::translationKeyProperty)
    private val spec1Property = bind(WowClass::spec1Property)
    private val spec2Property = bind(WowClass::spec2Property)
    private val spec3Property = bind(WowClass::spec3Property)
    val spec1 = SpecializationModel()
    val spec2 = SpecializationModel()
    val spec3 = SpecializationModel()

    init {
        spec1.itemProperty.bind(spec1Property)
        spec2.itemProperty.bind(spec2Property)
        spec3.itemProperty.bind(spec3Property)
    }
}