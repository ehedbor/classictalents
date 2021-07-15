package org.hedbor.evan.classictalents.app.model

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import javafx.scene.layout.*
import org.hedbor.evan.classictalents.common.model.Location
import org.hedbor.evan.classictalents.common.model.Specialization
import org.hedbor.evan.classictalents.common.model.Talent
import org.hedbor.evan.classictalents.common.model.WowClass
import tornadofx.*


class SpecializationViewModel(initialClass: WowClass, initialSpec: Specialization) : ViewModel() {
    val wowClassProperty = SimpleObjectProperty(initialClass)
    var wowClass by wowClassProperty

    val specializationProperty = SimpleObjectProperty(initialSpec)
    var specialization by specializationProperty

    val talents = specialization.talentsProperty

    val backgroundImage = specialization.backgroundImageProperty.objectBinding { path ->
        if (path == null) return@objectBinding null
        val image = runCatching { Image(path) }.getOrNull() ?: return@objectBinding null

        // TODO: Find a way to specify this part via CSS while still allowing the background image to be dynamically modified
        val repeat = BackgroundRepeat.NO_REPEAT
        val pos = BackgroundPosition.CENTER
        val size = BackgroundSize(1.0, 1.0, true, true, false, false)
        val backgroundImage = BackgroundImage(image, repeat, repeat, pos, size)
        Background(backgroundImage)
    }

    init {
        rebindOnChange(wowClassProperty)
        rebindOnChange(specializationProperty)
    }

    fun getViewModel(talent: Talent) = TalentButtonViewModel(wowClass, specialization, talent)

    fun getPrerequisiteLocationFor(dependencyLocation: Location): Location? {
        val dep = talents.firstOrNull { it.location == dependencyLocation } ?: return null
        val req = talents.firstOrNull { it.location == dep.prerequisite }
        return req?.location
    }
}