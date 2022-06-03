package org.hedbor.evan.classictalents.model

import org.hedbor.evan.classictalents.util.emptyObservableList
import org.hedbor.evan.classictalents.util.getProperty
import org.hedbor.evan.classictalents.util.property


@Suppress("MemberVisibilityCanBePrivate")
class Specialization {
    var name by property<String>()
    fun nameProperty() = getProperty(Specialization::name)

    var icon by property<String>()
    fun iconProperty() = getProperty(Specialization::icon)

    var background by property<String>()
    fun backgroundProperty() = getProperty(Specialization::background)

    var talents by property(emptyObservableList<Talent>())
    fun talentsProperty() = getProperty(Specialization::talents)
}