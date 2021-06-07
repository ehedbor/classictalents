package org.hedbor.evan.talenttreegenerator.model

import tornadofx.ItemViewModel

class SpellModel : ItemViewModel<Spell>() {
    val hasResource = bind(Spell::hasResourceProperty)
    val resourceCost = bind(Spell::resourceCostProperty)
    val resourceType = bind(Spell::resourceTypeProperty)
    val isInstantCast = bind(Spell::isInstantCastProperty)
    val castTime = bind(Spell::castTimeProperty)
    val hasCooldown = bind(Spell::hasCooldownProperty)
    val cooldown = bind(Spell::cooldownProperty)
    val cooldownUnit = bind(Spell::cooldownUnitProperty)
    val isNotMeleeRange = bind(Spell::isNotMeleeRangeProperty)
    val range = bind(Spell::rangeProperty)
}