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

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hedbor.evan.classictalents.common.model.WowClassData
import org.hedbor.evan.classictalents.talentgen.model.WowClass
import java.io.File


object TalentGenSerializer {
    fun loadClass(file: File): WowClass {
        val text = file.readText()
        val data = Json.decodeFromString<WowClassData>(text)
        return WowClass().fromData(data)
    }

    fun saveClass(wowClass: WowClass, file: File) {
        val text = Json.encodeToString(wowClass.toData())
        file.parentFile.mkdirs()
        file.writeText(text)
    }
}