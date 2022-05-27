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

package org.hedbor.evan.classictalents.app.service

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.hedbor.evan.classictalents.app.model.WowClass
import org.hedbor.evan.classictalents.app.util.MultiResourceBundle
import org.hedbor.evan.classictalents.common.model.WowClassData
import java.io.IOException
import java.util.*


object FileService {
    fun loadClasses(vararg paths: String): List<WowClass> {
        return paths.map { path ->
            val stream = this::class.java.getResourceAsStream(path)
                ?: throw IOException("Fatal Error: Could not load resource at \"$path\".")
            val rawText = stream.bufferedReader().use { it.readText() }
            val wowClassData = Json.decodeFromString<WowClassData>(rawText)
            return@map WowClass().fromData(wowClassData)
        }
    }

    fun loadBundles(baseName: String, vararg delegateBaseNames: String): ResourceBundle {
        val control = MultiResourceBundle.Control(delegateBaseNames)
        return ResourceBundle.getBundle(baseName, control)
    }
}
