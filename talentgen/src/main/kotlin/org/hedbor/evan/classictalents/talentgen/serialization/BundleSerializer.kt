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
    private val orderedEntries = LinkedHashMap<Any?, Any?>()

    override fun keys() = Collections.enumeration(orderedEntries.keys) as Enumeration<Any?>
    override val keys = orderedEntries.keys
    override val values = orderedEntries.values
    override val entries = orderedEntries.entries

    override fun put(key: Any?, value: Any?): Any? {
        orderedEntries[key] = value
        return super.put(key, value)
    }

    override fun remove(key: Any?): Any? {
        orderedEntries.remove(key)
        return super.remove(key)
    }

    override fun clear() {
        orderedEntries.clear()
        super.clear()
    }
}