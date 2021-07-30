/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2021 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.common.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hedbor.evan.classictalents.common.model.WowClass
import java.io.File
import java.io.InputStream


object ClassicTalentsSerializer {
    @OptIn(ExperimentalSerializationApi::class)
    private val jsonParser by lazy {
        /*Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }*/
        Json
    }

    fun loadClass(dataFile: File): WowClass {
        return jsonParser.decodeFromString(dataFile.readText())
    }

    fun loadClassAsStream(dataStream: InputStream): WowClass {
        val data = dataStream.bufferedReader().use { it.readText() }
        return jsonParser.decodeFromString(data)
    }

    fun saveClass(wowClass: WowClass, dataFile: File) {
        dataFile.writeText(jsonParser.encodeToString(wowClass))
    }
}