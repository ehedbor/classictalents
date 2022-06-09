package org.hedbor.evan.classictalents.dto

import com.fasterxml.jackson.annotation.JsonProperty


typealias TalentConfigDto = Map<String, ClassDto>

data class ClassDto(
    @JsonProperty("Icon") val icon: String,
    @JsonProperty("Color") val color: String,
    @JsonProperty("Classic") val classic: ClassicEraDto,
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
    @JsonProperty("Requires") val requires: String? = null,
    @JsonProperty("Max Rank") val maxRank: Int,
    @JsonProperty("Icon") val icon: String,
    @JsonProperty("Description") val description: String,
    @JsonProperty("Spell") val spell: SpellDto? = null,
)

data class SpellDto(
    @JsonProperty("Tools") val tools: List<String> = emptyList(),
    @JsonProperty("Reagents") val reagents: List<String> = emptyList(),
    @JsonProperty("Cost") val cost: String? = null,
    @JsonProperty("Range") val range: String? = null,
    @JsonProperty("Cast Time") val castTime: String,
    @JsonProperty("Cooldown") val cooldown: String? = null
)

