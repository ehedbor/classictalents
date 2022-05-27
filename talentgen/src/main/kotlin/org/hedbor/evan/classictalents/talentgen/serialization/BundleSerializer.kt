/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2022 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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