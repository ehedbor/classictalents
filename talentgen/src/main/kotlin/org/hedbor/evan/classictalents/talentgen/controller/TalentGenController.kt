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

package org.hedbor.evan.classictalents.talentgen.controller

import javafx.application.Platform
import javafx.stage.FileChooser
import org.hedbor.evan.classictalents.common.model.WowClass
import org.hedbor.evan.classictalents.common.serialization.ClassicTalentsSerializer
import org.hedbor.evan.classictalents.talentgen.model.Bundle
import org.hedbor.evan.classictalents.talentgen.model.BundleEntry
import org.hedbor.evan.classictalents.talentgen.model.BundleModel
import org.hedbor.evan.classictalents.talentgen.model.WowClassModel
import org.hedbor.evan.classictalents.talentgen.serialization.BundleSerializer
import tornadofx.*
import java.io.File
import kotlin.system.exitProcess


class TalentGenController : Controller() {
    companion object {
        private val TALENTS_OUTPUT_DIR = File("./out/talents")
        private val LANG_OUTPUT_DIR = File("./out/bundles")
        private val JSON_FILE_FILTER = FileChooser.ExtensionFilter("Json Files", "*.json")
        private val PROPERTIES_FILE_FILTER = FileChooser.ExtensionFilter("Java Properties Files", "*.properties")

        private fun Bundle.addMissingEntries(clazz: WowClass) {
            if (clazz.translationKey.isNotBlank())
                addDefaultEntry(clazz.translationKey)

            for (spec in clazz.specializations.filter { it.translationKey.isNotBlank() }) {
                addDefaultEntry(clazz.translationKey, spec.translationKey)

                for (talent in spec.talents.filter { it.translationKey.isNotBlank() }) {
                    addDefaultEntry(clazz.translationKey, spec.translationKey, talent.translationKey, addDesc = true)
                }
            }
        }

        private fun Bundle.addDefaultEntry(firstKey: String, vararg keys: String, addDesc: Boolean = false) {
            val allKeys = arrayOf(firstKey, *keys)
            val fullKey = allKeys.joinToString(".")

            if (entries.any { it.translationKey == fullKey }) {
                return
            }

            // the most likely words to occur that should not be capitalized
            val blacklist = listOf("a", "an", "the", "for", "and", "to", "in", "on")

            val defaultDisplayName = allKeys
                .last()
                .split('_')
                .joinToString(" ") { word ->
                    if (word in blacklist) word
                    else word.replaceFirstChar { it.titlecase() }
                }.replaceFirstChar { it.titlecase() }

            entries += BundleEntry(fullKey, defaultDisplayName)
            if (addDesc) {
                entries += BundleEntry("$fullKey.desc", "")
            }
        }
    }

    var classModel = WowClassModel(WowClass(translationKey = "new_class"))
        private set
    
    var bundleModel = BundleModel(Bundle())
        private set

    private var currentDataFile: File? = null
    private var currentBundleFile: File? = null

    fun newDataFile(): Boolean {
        var shouldClearData = false

        if (!classModel.isDirty) {
            shouldClearData = true
        } else {
            confirm(
                messages["confirm.discard"],
                messages.format("confirm.discard.content", messages["this.class"])
            ) {
                shouldClearData = true
            }
        }

        if (shouldClearData) {
            classModel.item = WowClass(translationKey = "new_class")
        }
        return shouldClearData
    }

    fun openDataFile(openRecent: Boolean): Boolean {
        val dataFileExists = currentDataFile?.exists() ?: false
        if (!openRecent || (openRecent && !dataFileExists)) {
            selectDataFile(FileChooserMode.Single)
        }

        return when (val file = currentDataFile) {
            null -> false
            else -> {
                classModel.item = ClassicTalentsSerializer.loadClass(file)
                true
            }
        }
    }

    fun saveDataFile(toNewFile: Boolean): Boolean {
        val dataFileExists = currentDataFile?.exists() ?: false
        if (toNewFile || !dataFileExists) {
            val suggestedFileName = "${classModel.translationKey.value}.json"
            selectDataFile(FileChooserMode.Save, suggestedFileName)
        }

        return when (val file = currentDataFile) {
            null -> false
            else -> {
                ClassicTalentsSerializer.saveClass(classModel.item, file)
                true
            }
        }
    }

    fun quit() {
        fun endProgram() {
            Platform.exit()
            exitProcess(0)
        }

        if (!classModel.isDirty) {
            endProgram()
        } else {
            confirm(
                messages["confirm.discard"],
                messages.format("confirm.discard.content", messages["this.class"])
            ) {
                endProgram()
            }
        }
    }

    fun newBundle() {
        currentBundleFile = null
        bundleModel.item = Bundle()
        bundleModel.item.addMissingEntries(classModel.item)
    }

    fun openBundle(openRecent: Boolean): Boolean {
        val bundleFileExists = currentBundleFile?.exists() ?: false
        if (!openRecent || (openRecent && !bundleFileExists)) {
            selectResourceBundle(FileChooserMode.Single)
        }

        return when (val file = currentBundleFile) {
            null -> false
            else -> {
                bundleModel.item = BundleSerializer.load(file)
                bundleModel.item.addMissingEntries(classModel.item)
                true
            }
        }
    }

    fun saveBundle(toNewFile: Boolean): Boolean {
        val bundleFileExists = currentBundleFile?.exists() ?: false
        if (toNewFile || !bundleFileExists) {
            val className = classModel.translationKey.value
            val locale = bundleModel.item.locale.toString()

            var suggestedFileName = className
            if (locale.isNotEmpty()) { suggestedFileName += "_$locale" }
            suggestedFileName += ".properties"

            selectResourceBundle(FileChooserMode.Save, suggestedFileName)
        }

        return when (currentBundleFile) {
            null -> false
            else -> {
                BundleSerializer.save(bundleModel.item, currentBundleFile!!)
                true
            }
        }
    }

    private fun selectDataFile(mode: FileChooserMode, suggestedFileName: String? = null) {
        TALENTS_OUTPUT_DIR.mkdirs()
        val files = chooseFile(messages["action.file.choose.data"], arrayOf(JSON_FILE_FILTER), TALENTS_OUTPUT_DIR, mode) {
            if (suggestedFileName != null) {
                initialFileName = suggestedFileName
            }
        }
        val f = files.firstOrNull()
        if (f != null) {
            currentDataFile = f
        }
    }

    private fun selectResourceBundle(mode: FileChooserMode, suggestedFileName: String? = null) {
        LANG_OUTPUT_DIR.mkdirs()
        val files = chooseFile(messages["action.file.choose.resource_bundle"], arrayOf(PROPERTIES_FILE_FILTER), LANG_OUTPUT_DIR, mode) {
            if (suggestedFileName != null) {
                initialFileName = suggestedFileName
            }
        }
        val f = files.firstOrNull()
        if (f != null) {
            currentBundleFile = f
        }
    }
}