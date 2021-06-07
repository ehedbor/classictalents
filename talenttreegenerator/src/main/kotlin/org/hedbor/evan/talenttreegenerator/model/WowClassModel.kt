package org.hedbor.evan.talenttreegenerator.model

import tornadofx.ItemViewModel


class WowClassModel : ItemViewModel<WowClass>() {
    val displayName = bind(WowClass::displayNameProperty)
    val translationKey = bind(WowClass::translationKeyProperty)
    val spec1 = bind(WowClass::spec1Property)
    val spec2 = bind(WowClass::spec2Property)
    val spec3 = bind(WowClass::spec3Property)
}