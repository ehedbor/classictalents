package org.hedbor.evan.classictalents.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/** @see Specialization.TALENT_COLUMN_COUNT */
@Serializable
enum class Era(val translationKey: String, val talentRowCount: Int) {
    @SerialName("classic")  CLASSIC("era.classic", 7),
    @SerialName("tbc")      TBC("era.tbc", 9),
    @SerialName("wotlk")    WOTLK("era.wotlk", 11);

    override fun toString() = translationKey
}