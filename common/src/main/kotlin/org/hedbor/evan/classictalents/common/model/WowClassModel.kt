package org.hedbor.evan.classictalents.common.model

import tornadofx.ItemViewModel


class WowClassModel(initialValue: WowClass? = null) : ItemViewModel<WowClass>(initialValue) {
    val displayName = bind(WowClass::displayNameProperty)
    val translationKey = bind(WowClass::translationKeyProperty)
    val era = bind(WowClass::eraProperty)
    val specializations = bind(WowClass::specializationsProperty)
}