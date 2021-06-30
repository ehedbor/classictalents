package org.hedbor.evan.classictalents.common.model

import javafx.beans.property.*
import kotlinx.serialization.Serializable
import org.hedbor.evan.classictalents.common.serialization.SpellSerializer
import tornadofx.*


@Suppress("MemberVisibilityCanBePrivate")
@Serializable(with = SpellSerializer::class)
class Spell(
    resourceCost: Int = 0,
    resourceType: ResourceType? = null,
    castTime: Double = 0.0,
    cooldown: Double = 0.0,
    cooldownUnit: CooldownUnit? = null,
    range: Double = 0.0
) {
    val resourceCostProperty = SimpleIntegerProperty(this, "resourceCost", resourceCost)
    var resourceCost: Int by resourceCostProperty

    val resourceTypeProperty = SimpleObjectProperty<ResourceType>(this, "resourceType", resourceType)
    var resourceType: ResourceType? by resourceTypeProperty

    val castTimeProperty = SimpleDoubleProperty(this, "castTime", castTime)
    var castTime: Double by castTimeProperty

    val cooldownProperty = SimpleDoubleProperty(this, "cooldown", cooldown)
    var cooldown: Double by cooldownProperty

    val cooldownUnitProperty = SimpleObjectProperty<CooldownUnit>(this, "cooldownUnit", cooldownUnit)
    var cooldownUnit: CooldownUnit? by cooldownUnitProperty

    val rangeProperty = SimpleDoubleProperty(this, "range", range)
    var range: Double by rangeProperty
}

enum class ResourceType(val key: String) {
    MANA("mana"),
    PERCENT_OF_BASE_MANA("percent_of_base_mana"),
    RAGE("rage"),
    ENERGY("energy");

    override fun toString() = key
}

enum class CooldownUnit(val key: String) {
    HOURS("hr"),
    MINUTES("min"),
    SECONDS("sec");

    override fun toString() = key
}

object Range {
    const val SELF = 0.0
    const val MELEE = 5.0

    fun isSelf(range: Double): Boolean {
        require(range >= 0.0) { "Range must be positive or zero." }
        return range == 0.0
    }

    fun isMelee(range: Double): Boolean {
        require(range >= 0.0) { "Range must be positive or zero." }
        return range > 0.0 && range <= 5.0
    }
}