package org.hedbor.evan.classictalents.model

import javafx.scene.paint.Color
import org.hedbor.evan.classictalents.util.emptyObservableList
import org.hedbor.evan.classictalents.util.getProperty
import org.hedbor.evan.classictalents.util.observableListOf
import org.hedbor.evan.classictalents.util.property

@Suppress("MemberVisibilityCanBePrivate")
class WowClass {
    var name by property<String>()
    fun nameProperty() = getProperty(WowClass::name)

    var icon by property<String>()
    fun iconProperty() = getProperty(WowClass::icon)

    var color by property<Color>()
    fun colorProperty() = getProperty(WowClass::color)

    var specializations by property(observableListOf<Specialization>())
    fun specializationsProperty() = getProperty(WowClass::specializations)
}