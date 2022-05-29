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

package org.hedbor.evan.classictalents.app.util

import java.util.*


class MultiResourceBundle(private val delegates: List<ResourceBundle> = emptyList()) : ResourceBundle() {
    class Control(private val delegateBaseNames: Array<out String>) : ResourceBundle.Control() {
        override fun newBundle(
            baseName: String,
            locale: Locale,
            format: String,
            loader: ClassLoader,
            reload: Boolean
        ): ResourceBundle {
            val delegates = delegateBaseNames
                .filter { it.isNotBlank() }
                .map { getBundle(it, locale, loader) }
            return MultiResourceBundle(delegates)
        }
    }

    override fun handleGetObject(key: String): Any? {
        return delegates
            .filter { it.containsKey(key) }
            .map { it.getObject(key) }
            .firstOrNull()
    }

    override fun getKeys(): Enumeration<String> {
        val keys = delegates.flatMap { it.keys.toList() }
        return Collections.enumeration(keys)
    }
}