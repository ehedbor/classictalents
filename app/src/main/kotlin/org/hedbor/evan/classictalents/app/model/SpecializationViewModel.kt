/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2021 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.app.model

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import javafx.scene.layout.*
import org.hedbor.evan.classictalents.app.view.styles.StyleConstants
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

    val talents = bind { specialization.talentsProperty }

    // TODO: find a way to share spec properties with talents -- have TalentButtonViewModel take a SpecViewModel, perhaps?
    private val totalPointsForSpec = run {
        val allTalentRanks = specialization.talentsProperty.map { it.rankProperty }.toTypedArray()
        Bindings.createIntegerBinding({ allTalentRanks.sumOf { it.value } }, *allTalentRanks)
    }

    private val classKey = bind { wowClass.translationKeyProperty }
    private val specKey = classKey.stringBinding(specialization.translationKeyProperty) { "$it.${specialization.translationKey}" }
    val specTitleText = specKey.stringBinding { messages[it!!] }

    val pointCounterText = totalPointsForSpec.stringBinding { "($it)" }

    val backgroundImage = specialization.backgroundImageProperty.objectBinding { path ->
        if (path == null) return@objectBinding null
        val image = runCatching { Image(path) }.getOrNull() ?: return@objectBinding null

        // TODO: Find a way to specify this part via CSS while still allowing the background image to be dynamically modified
        val repeat = BackgroundRepeat.NO_REPEAT
        val pos = BackgroundPosition.CENTER
        val size = BackgroundSize(1.0, 1.0, true, true, true, true)
        val backgroundImage = BackgroundImage(image, repeat, repeat, pos, size)
        val backgroundFill = BackgroundFill(StyleConstants.LIGHT_BACKGROUND_COLOR, null, null)
        Background(listOf(backgroundFill), listOf(backgroundImage))
    }

    // TODO: load an icon image somehow
    val iconImage = SimpleObjectProperty(Image("/images/Classic/Spells/Spell_Fire_MeteorStorm.png"))
    val borderImage = SimpleObjectProperty(Image("/images/Icon/large/border/default.png"))

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