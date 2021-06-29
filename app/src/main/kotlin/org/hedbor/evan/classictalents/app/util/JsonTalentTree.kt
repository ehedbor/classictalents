package org.hedbor.evan.classictalents.app.util

import com.beust.klaxon.Json

data class JsonWowClass(
    @Json(name = "class") val key: String,
    val spec1: JsonTalentTree,
    //val spec2: JsonTalentTree,
    //val spec3: JsonTalentTree
)

data class JsonTalentTree(
    val backgroundImage: String,
    val talents: Map<String, JsonTalent>
)

data class JsonTalent(
    val location: List<Int>,
    val maxRank: Int,
    val icon: String,
    @Json(name = "requires")
    val prerequisite: List<Int>? = null,
    val spell: JsonSpellInfo? = null
)

data class JsonSpellInfo(
    val resourceCost: String? = null,
    val range: String? = null,
    val castTime: String,
    val cooldown: String? = null
)