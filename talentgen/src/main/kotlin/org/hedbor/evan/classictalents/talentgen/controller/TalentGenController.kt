package org.hedbor.evan.classictalents.talentgen.controller

import javafx.application.Platform
import javafx.stage.FileChooser
import org.hedbor.evan.classictalents.common.model.WowClass
import org.hedbor.evan.classictalents.common.model.WowClassModel
import org.hedbor.evan.classictalents.common.serialization.ClassicTalentsSerializer
import org.hedbor.evan.classictalents.talentgen.model.Bundle
import org.hedbor.evan.classictalents.talentgen.model.BundleEntry
import org.hedbor.evan.classictalents.talentgen.model.BundleModel
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
            addDefaultEntry(clazz.translationKey)

            for (spec in clazz.specializations) {
                addDefaultEntry(clazz.translationKey, spec.translationKey)

                for (talent in spec.talents) {
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

    var classModel = WowClassModel(WowClass { translationKey = "new_class" })
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
            classModel.item = WowClass { translationKey = "new_class" }
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
            selectDataFile(FileChooserMode.Save)
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
            var locale = bundleModel.item.locale.toString()
            if (locale.isNotEmpty()) { locale = "_$locale" }
            val suggestedFileName = "$className$locale.properties"
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

    private fun selectDataFile(mode: FileChooserMode) {
        TALENTS_OUTPUT_DIR.mkdirs()
        val files = chooseFile(messages["action.file.choose.data"], arrayOf(JSON_FILE_FILTER), TALENTS_OUTPUT_DIR, mode)
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