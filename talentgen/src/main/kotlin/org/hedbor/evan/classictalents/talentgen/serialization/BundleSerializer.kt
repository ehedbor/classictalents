package org.hedbor.evan.classictalents.talentgen.serialization

import org.hedbor.evan.classictalents.talentgen.model.Bundle
import org.hedbor.evan.classictalents.talentgen.model.BundleEntry
import java.io.File
import java.util.*


object BundleSerializer {
    fun load(file: File): Bundle {
        val result = Bundle()

        val locale = file.nameWithoutExtension.replaceBefore('_', "")
        result.locale = Locale(locale)
        
        val properties = OrderedProperties()
        file.bufferedReader().use { properties.load(it) }
        for ((key, value) in properties) {
            result.entries += BundleEntry(key as String, value as String)
        }

        return result
    }

    fun save(bundle: Bundle, file: File) {
        val properties = OrderedProperties()
        for ((key, value) in bundle.entries) {
            properties.setProperty(key, value)
        }
        file.bufferedWriter().use { properties.store(it, null) }
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