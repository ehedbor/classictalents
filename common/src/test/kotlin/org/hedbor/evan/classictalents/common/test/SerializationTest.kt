package org.hedbor.evan.classictalents.common.test

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.numericDoubles
import io.kotest.property.checkAll
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hedbor.evan.classictalents.common.model.*
import tornadofx.observableListOf


class SerializationTest : ShouldSpec({
    val serializer = Json {
        encodeDefaults = false
    }

    context("location serializer") {
        should("serialize") {
            checkAll(
                Arb.int(0 until Era.WOTLK.talentRowCount),
                Arb.int(0 until Specialization.TALENT_COLUMN_COUNT)
            ) { row, col ->
                val location = Location(row, col)
                val encoded = serializer.encodeToString(location)
                val decoded = serializer.decodeFromString<Location>(encoded)

                encoded shouldBe "[$row,$col]"
                decoded shouldBe location
            }
        }
    }
    context("spell serializer") {
        should("serialize defaults") {
            val spell = Spell()
            val encoded = serializer.encodeToString(spell)
            val decoded = serializer.decodeFromString<Spell>(encoded)

            encoded shouldBe """{"castTime":"instant"}"""
            decoded shouldBe spell
        }
        should("serialize special values") {
            val spell = Spell(
                400,
                null,
                0.0,
                30.0,
                null,
                5.0
            )
            val encoded = serializer.encodeToString(spell)
            val decoded = serializer.decodeFromString<Spell>(encoded)

            encoded shouldBe """{"range":"melee","castTime":"instant"}"""
            decoded shouldBe Spell(range = 5.0)
        }
        should("serialize") {
            checkAll(
                Arb.int(1..Int.MAX_VALUE),
                Arb.enum<ResourceType>(),
                Arb.numericDoubles(0.2),
                Arb.numericDoubles(1.0),
                Arb.enum<CooldownUnit>(),
                Arb.numericDoubles(7.0),
            ) { resourceCost, resourceType, castTime, cooldown, cooldownUnit, range ->
                val spell = Spell(
                    resourceCost,
                    resourceType,
                    castTime,
                    cooldown,
                    cooldownUnit,
                    range
                )
                val encoded = serializer.encodeToString(spell)
                val decoded = serializer.decodeFromString<Spell>(encoded)

                encoded shouldBe """{"resourceCost":"$resourceCost ${resourceType.serialName}","range":"$range yd","castTime":"$castTime sec","cooldown":"$cooldown ${cooldownUnit.serialName}"}"""
                decoded shouldBe spell
            }
        }
    }
    context("talent serializer") {
        should("serialize defaults") {
            val talent = Talent()
            val encoded = serializer.encodeToString(talent)
            val decoded = serializer.decodeFromString<Talent>(encoded)

            encoded shouldBe """{"key":"","location":[0,0],"maxRank":0,"icon":""}"""
            decoded shouldBe talent
        }
    }
    context("specialization serializer") {
        should("serialize defaults") {
            val spec = Specialization()
            val encoded = serializer.encodeToString(spec)
            val decoded = serializer.decodeFromString<Specialization>(encoded)

            encoded shouldBe """{"key":"","backgroundImage":"","talents":[]}"""
            decoded shouldBe spec
        }
    }
    context("wow class serializer") {
        should("serialize defaults") {
            val wowClass = WowClass()
            val encoded = serializer.encodeToString(wowClass)
            val decoded = serializer.decodeFromString<WowClass>(encoded)

            encoded shouldBe """{"key":"","era":"classic","specs":[]}"""
            decoded shouldBe wowClass
        }
        should("serialize full example") {
            val wowClass = WowClass("classname", Era.TBC, observableListOf(
                Specialization("specname", "background.png", observableListOf(
                    Talent("talentname", Location(3, 4), maxRank = 1, icon = "icon.png",
                        spell = Spell(resourceCost = 20, resourceType = ResourceType.ENERGY))
                ))
            ))

            val encoded = serializer.encodeToString(wowClass)
            val decoded = serializer.decodeFromString<WowClass>(encoded)

            encoded shouldBe """{"key":"classname","era":"tbc","specs":[{"key":"specname","backgroundImage":"background.png","talents":[{"key":"talentname","location":[3,4],"maxRank":1,"icon":"icon.png","spell":{"resourceCost":"20 energy","castTime":"instant"}}]}]}"""
            decoded shouldBe wowClass
        }
    }
})

