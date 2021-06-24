package org.hedbor.evan.talenttreegenerator.model

import tornadofx.ItemViewModel

class SpellModel(initialValue: Spell? = null) : ItemViewModel<Spell>(initialValue) {
    val hasResource = bind(Spell::hasResourceProperty)
    val resourceCost = bind(Spell::resourceCostProperty)
    val resourceType = bind(Spell::resourceTypeProperty)
    val isNotInstantCast = bind(Spell::isNotInstantCastProperty)
    val castTime = bind(Spell::castTimeProperty)
    val hasCooldown = bind(Spell::hasCooldownProperty)
    val cooldown = bind(Spell::cooldownProperty)
    val cooldownUnit = bind(Spell::cooldownUnitProperty)
    val hasRange = bind(Spell::hasRangeProperty)
    val range = bind(Spell::rangeProperty)
}