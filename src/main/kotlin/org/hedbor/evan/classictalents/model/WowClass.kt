package org.hedbor.evan.classictalents.model

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.image.Image
import javafx.scene.paint.Color
import org.hedbor.evan.classictalents.util.getProperty
import org.hedbor.evan.classictalents.util.intBinding
import org.hedbor.evan.classictalents.util.observableListOf
import org.hedbor.evan.classictalents.util.property

@Suppress("MemberVisibilityCanBePrivate")
class WowClass {
    var name by property<String>()
    fun nameProperty() = getProperty(WowClass::name)

    var icon by property<Image>()
    fun iconProperty() = getProperty(WowClass::icon)

    var color by property<Color>()
    fun colorProperty() = getProperty(WowClass::color)

    var specializations by property(observableListOf<Specialization>())
    fun specializationsProperty() = getProperty(WowClass::specializations)

    private val _allocatedPointsProperty = SimpleIntegerProperty().apply {
        this.bind(specializationsProperty().intBinding { specs ->
            specs.sumOf { it.allocatedPoints }
        })
    }
    val allocatedPoints get() = _allocatedPointsProperty.get()
    fun allocatedPointsProperty() = _allocatedPointsProperty
}