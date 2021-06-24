package org.hedbor.evan.talenttreegenerator.model.serializers

import org.hedbor.evan.talenttreegenerator.model.WowClass
import java.io.File
import java.util.*
import kotlin.collections.LinkedHashSet


object TranslationsSerializer {
    fun read(wowClass: WowClass, source: File) {
        val properties = OrderedProperties()
        properties.load(source.bufferedReader())

        wowClass.displayName = properties.getProperty(wowClass.translationKey)

        for (spec in wowClass.specializations) {
            val specKey = wowClass.translationKey + "." + spec.translationKey
            spec.displayName = properties.getProperty(specKey)

            for (talent in spec.talents) {
                if (talent.translationKey.isNullOrEmpty()) continue

                val talentKey = specKey + "." + talent.translationKey
                talent.displayName = properties.getProperty(talentKey)
                talent.description = properties.getProperty("$talentKey.desc")
            }
        }
    }

    fun write(wowClass: WowClass, destination: File) {
        val properties = OrderedProperties()
        properties.setProperty(wowClass.translationKey, wowClass.displayName)

        for (spec in wowClass.specializations) {
            val specKey = wowClass.translationKey + "." + spec.translationKey
            properties.setProperty(specKey, spec.displayName)

            for (talent in spec.talents) {
                if (talent.displayName.isNullOrEmpty()) continue

                val talentKey = specKey + "." + talent.translationKey
                properties.setProperty(talentKey, talent.displayName)
                properties.setProperty("$talentKey.desc", talent.description)
            }
        }

        properties.store(destination.bufferedWriter(), null)
    }
}

private class OrderedProperties : Properties() {
    val orderedKeys = LinkedHashSet<Any?>()

    @Synchronized
    override fun keys(): Enumeration<Any> {
        return Collections.enumeration(orderedKeys)
    }

    @Synchronized
    override fun put(key: Any?, value: Any?): Any? {
        orderedKeys += key
        return super.put(key, value)
    }
}