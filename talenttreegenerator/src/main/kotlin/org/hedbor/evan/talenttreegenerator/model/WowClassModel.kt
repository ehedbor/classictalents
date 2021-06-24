package org.hedbor.evan.talenttreegenerator.model

import tornadofx.ItemViewModel


class WowClassModel(initialValue: WowClass? = null) : ItemViewModel<WowClass>(initialValue) {
    val displayName = bind(WowClass::displayNameProperty)
    val translationKey = bind(WowClass::translationKeyProperty)
    val specializations = bind(WowClass::specializationsProperty)
}