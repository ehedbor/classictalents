package org.hedbor.evan.classictalents.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.image.Image
import javafx.scene.layout.Background
import org.hedbor.evan.classictalents.util.getProperty
import org.hedbor.evan.classictalents.util.intBinding
import org.hedbor.evan.classictalents.util.observableListOf
import org.hedbor.evan.classictalents.util.property


@Suppress("MemberVisibilityCanBePrivate")
class Specialization {
    var name by property<String>()
    fun nameProperty() = getProperty(Specialization::name)

    var icon by property<Image>()
    fun iconProperty() = getProperty(Specialization::icon)

    var background by property<Image>()
    fun backgroundProperty() = getProperty(Specialization::background)

    var talents by property(observableListOf<Talent>())
    fun talentsProperty() = getProperty(Specialization::talents)

    private val _allocatedPointsProperty = SimpleIntegerProperty().apply {
        this.bind(talentsProperty().intBinding { talents ->
            talents.sumOf { it.rank }
        })
    }
    val allocatedPoints get() = _allocatedPointsProperty.get()
    fun allocatedPointsProperty() = _allocatedPointsProperty
}