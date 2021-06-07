package org.hedbor.evan.talenttreegenerator.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.getValue
import tornadofx.setValue

class Spell(
    hasResource: Boolean = false,
    resourceCost: Int = Int.MIN_VALUE,
    resourceType: String? = null,
    isInstantCast: Boolean = false,
    castTime: Double = Double.MIN_VALUE,
    hasCooldown: Boolean = false,
    cooldown: Int = Int.MIN_VALUE,
    cooldownUnit: String? = null,
    isNotMeleeRange: Boolean = false,
    range: Int = Int.MIN_VALUE
) {
    val hasResourceProperty = SimpleBooleanProperty(this, "hasResource", hasResource)
    var hasResource by hasResourceProperty

    val resourceCostProperty = SimpleIntegerProperty(this, "resourceCost", resourceCost)
    var resourceCost by resourceCostProperty

    val resourceTypeProperty = SimpleStringProperty(this, "resourceType", resourceType)
    var resourceType by resourceTypeProperty

    val isInstantCastProperty = SimpleBooleanProperty(this, "isInstantCast", isInstantCast)
    var isInstantCast by isInstantCastProperty

    val castTimeProperty = SimpleDoubleProperty(this, "castTime", castTime)
    var castTime by castTimeProperty

    val hasCooldownProperty = SimpleBooleanProperty(this, "hasCooldown", hasCooldown)
    var hasCooldown by hasCooldownProperty

    val cooldownProperty = SimpleIntegerProperty(this, "cooldown", cooldown)
    var cooldown by cooldownProperty

    val cooldownUnitProperty = SimpleStringProperty(this, "cooldownUnit", cooldownUnit)
    var cooldownUnit by cooldownUnitProperty

    val isNotMeleeRangeProperty = SimpleBooleanProperty(this, "isNotMeleeRange", isNotMeleeRange)
    var isNotMeleeRange by isNotMeleeRangeProperty

    val rangeProperty = SimpleIntegerProperty(this, "range", range)
    var range by rangeProperty
}
