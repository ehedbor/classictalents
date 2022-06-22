package org.hedbor.evan.classictalents.dto

import com.fasterxml.jackson.annotation.JsonProperty


typealias TalentConfigDto = Map<String, ClassDto>

data class ClassDto(
    @JsonProperty("Icon") val icon: String,
    @JsonProperty("Color") val color: String,
    @JsonProperty("Classic") val classic: ClassicEraDto?,
    @JsonProperty("TBC") val tbc: ClassicEraDto?,
    @JsonProperty("WotLK") val wotlk: ClassicEraDto?, // TODO: add glyphs
)

data class ClassicEraDto(
    @JsonProperty("Specializations") val specs: Map<String, SpecDto>,
)

data class SpecDto(
    @JsonProperty("Icon") val icon: String,
    @JsonProperty("Background") val background: String,
    @JsonProperty("Talents") val talents: Map<String, TalentDto>,
)

data class TalentDto(
    @JsonProperty("Location") val location: List<Int>,
    @JsonProperty("Requires") val requires: String?,
    @JsonProperty("Max Rank") val maxRank: Int,
    @JsonProperty("Icon") val icon: String,
    @JsonProperty("Description") val description: String,
    @JsonProperty("Spell") val spell: SpellDto?,
)

data class SpellDto(
    @JsonProperty("Tools") val tools: List<String>?,
    @JsonProperty("Reagents") val reagents: List<String>?,
    @JsonProperty("Cost") val cost: String?,
    @JsonProperty("Range") val range: String?,
    @JsonProperty("Cast Time") val castTime: String,
    @JsonProperty("Cooldown") val cooldown: String?,
)

