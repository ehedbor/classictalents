package org.hedbor.evan.classictalents.talentgen.model

import org.hedbor.evan.classictalents.common.model.WowClass
import tornadofx.ItemViewModel


class WowClassModel(initialValue: WowClass? = null) : ItemViewModel<WowClass>(initialValue) {
    val translationKey = bind(WowClass::translationKeyProperty)
    val era = bind(WowClass::eraProperty)
    val specializations = bind(WowClass::specializationsProperty)
}