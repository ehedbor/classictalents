package org.hedbor.evan.classictalents.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/** @see Specialization.TALENT_COLUMN_COUNT */
@Serializable
enum class Era(val translationKey: String, val talentRowCount: Int, val maxLevel: Int) {
    @SerialName("classic")  CLASSIC("era.classic", 7, 60),
    @SerialName("tbc")      TBC("era.tbc", 9, 70),
    @SerialName("wotlk")    WOTLK("era.wotlk", 11, 80);

    override fun toString() = translationKey

    fun getAvailablePoints(level: Int): Int {
        require(level in 1..maxLevel)
        if (level < 10) return 0
        return level - 9
    }
}