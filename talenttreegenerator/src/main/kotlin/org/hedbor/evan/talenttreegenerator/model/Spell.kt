package org.hedbor.evan.talenttreegenerator.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.Serializable
import org.hedbor.evan.talenttreegenerator.model.serializers.SpellSerializer
import tornadofx.getValue
import tornadofx.setValue

@Suppress("unused", "HasPlatformType")
@Serializable(with = SpellSerializer::class)
class Spell(
    hasResource: Boolean = false,
    resourceCost: Int = 0,
    resourceType: String? = null,
    isNotInstantCast: Boolean = false,
    castTime: Double = 0.0,
    hasCooldown: Boolean = false,
    cooldown: Double = 0.0,
    cooldownUnit: String? = null,
    hasRange: Boolean = false,
    range: Int = 0
) {
    val hasResourceProperty = SimpleBooleanProperty(this, "hasResource", hasResource)
    var hasResource by hasResourceProperty

    val resourceCostProperty = SimpleIntegerProperty(this, "resourceCost", resourceCost)
    var resourceCost by resourceCostProperty

    val resourceTypeProperty = SimpleStringProperty(this, "resourceType", resourceType)
    var resourceType by resourceTypeProperty

    val isNotInstantCastProperty = SimpleBooleanProperty(this, "isNotInstantCast", isNotInstantCast)
    var isNotInstantCast by isNotInstantCastProperty

    val castTimeProperty = SimpleDoubleProperty(this, "castTime", castTime)
    var castTime by castTimeProperty

    val hasCooldownProperty = SimpleBooleanProperty(this, "hasCooldown", hasCooldown)
    var hasCooldown by hasCooldownProperty

    val cooldownProperty = SimpleDoubleProperty(this, "cooldown", cooldown)
    var cooldown by cooldownProperty

    val cooldownUnitProperty = SimpleStringProperty(this, "cooldownUnit", cooldownUnit)
    var cooldownUnit by cooldownUnitProperty

    val hasRangeProperty = SimpleBooleanProperty(this, "hasRange", hasRange)
    var hasRange by hasRangeProperty

    val rangeProperty = SimpleIntegerProperty(this, "range", range)
    var range by rangeProperty

    override fun toString(): String {
        return """Spell(hasResource=$hasResource, resourceCost=$resourceCost, resourceType='$resourceType', 
            |isNotInstantCast=$isNotInstantCast, castTime=$castTime, hasCooldown=$hasCooldown, cooldown=$cooldown, 
            |cooldownUnit='$cooldownUnit', hasRange=$hasRange, range=$range)""".trimMargin()
    }
}
