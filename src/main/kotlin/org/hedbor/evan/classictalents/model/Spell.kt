package org.hedbor.evan.classictalents.model

import javafx.beans.property.*
import javafx.collections.ObservableList
import org.hedbor.evan.classictalents.util.delegate
import org.hedbor.evan.classictalents.util.observableListOf


@Suppress("MemberVisibilityCanBePrivate", "unused")
class Spell {
    companion object {
        const val RANGE_SELF = 0.0
        const val RANGE_MELEE = 5.0
        const val CAST_INSTANT = 0.0
        const val CAST_NEXT_MELEE = -1.0
    }

    private val _resourceCosts = SimpleListProperty(observableListOf<Pair<Int, ResourceType>>())
    var resourceCosts: ObservableList<Pair<Int, ResourceType>> by _resourceCosts.delegate()
    fun resourceCostsProperty() = _resourceCosts

    private val _range = SimpleObjectProperty<Double?>()
    var range by _range.delegate()
    fun rangeProperty() = _range

    private val _minRange = SimpleObjectProperty<Double?>()
    var minRange by _minRange.delegate()
    fun minRangeProperty() = _minRange

    private val _castTime = SimpleDoubleProperty()
    var castTime by _castTime.delegate()
    fun castTimeProperty() = _castTime

    private val _isChanneled = SimpleBooleanProperty()
    var isChanneled by _isChanneled.delegate()
    fun channeledProperty() = _isChanneled

    private val _cooldown = SimpleObjectProperty<Double?>()
    var cooldown by _cooldown.delegate()
    fun cooldownProperty() = _cooldown

    private val _cooldownUnit = SimpleObjectProperty<CooldownUnit?>()
    var cooldownUnit by _cooldownUnit.delegate()
    fun cooldownUnitProperty() = _cooldownUnit

    private val _reagents = SimpleListProperty<String>(observableListOf())
    var reagents: ObservableList<String> by _reagents.delegate()
    fun reagentsProperty() = _reagents

    private val _tools = SimpleListProperty<String>(observableListOf())
    var tools: ObservableList<String> by _tools.delegate()
    fun toolsProperty() = _tools
}

enum class ResourceType {
    MANA,
    PERCENT_OF_BASE_MANA,
    RAGE,
    ENERGY,
    BLOOD_RUNES,
    FROST_RUNES,
    UNHOLY_RUNES,
    RUNIC_POWER
}

enum class CooldownUnit {
    SECONDS,
    MINUTES,
    HOURS
}