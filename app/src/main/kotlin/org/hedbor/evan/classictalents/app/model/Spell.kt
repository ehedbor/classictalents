/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2022 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.app.model

import org.hedbor.evan.classictalents.common.model.CooldownUnit
import org.hedbor.evan.classictalents.common.model.Range
import org.hedbor.evan.classictalents.common.model.ResourceType
import org.hedbor.evan.classictalents.common.model.SpellData
import org.hedbor.evan.classictalents.common.serialization.ModelFor


class Spell(
    resourceCost: Int = 0,
    resourceType: ResourceType? = null,
    range: Range = Range.SELF,
    castTime: Double = 0.0,
    cooldown: Double = 0.0,
    cooldownUnit: CooldownUnit? = null,
) : ModelFor<SpellData> {
    var resourceCost = resourceCost 
        private set
    var resourceType = resourceType
        private set
    var range = range
        private set
    var castTime = castTime
        private set
    var cooldown = cooldown
        private set
    var cooldownUnit = cooldownUnit
        private set

    override fun toData(): SpellData {
        return SpellData(resourceCost, resourceType, range, castTime, cooldown, cooldownUnit)
    }

    override fun fromData(data: SpellData): Spell {
        resourceCost = data.resourceCost
        resourceType = data.resourceType
        range = data.range
        castTime = data.castTime
        cooldown = data.cooldown
        cooldownUnit = data.cooldownUnit
        
        return this
    }
}