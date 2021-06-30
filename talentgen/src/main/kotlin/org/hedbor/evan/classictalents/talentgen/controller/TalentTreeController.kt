package org.hedbor.evan.classictalents.talentgen.controller

import javafx.application.Platform
import javafx.stage.FileChooser
import org.hedbor.evan.classictalents.common.model.WowClass
import org.hedbor.evan.classictalents.common.serialization.ClassicTalentsSerializer
import org.hedbor.evan.classictalents.talentgen.model.WowClassModel
import tornadofx.Controller
import tornadofx.FileChooserMode
import tornadofx.chooseFile
import java.io.File
import kotlin.system.exitProcess


class TalentTreeController : Controller() {
    companion object {
        private val OUTPUT_DIR = File("./generated-talents")
        private val ALL_FILES_FILTER = FileChooser.ExtensionFilter("All Files", "*.*")
        private val JSON_FILE_FILTER = FileChooser.ExtensionFilter("Json Files", "*.json")
        private val PROPERTIES_FILE_FILTER = FileChooser.ExtensionFilter("Java Properties Files", "*.properties")
    }

    var model = WowClassModel(WowClass(displayName = "New Class"))
        private set

    private var currentDataFile: File? = null
    private var currentLangFile: File? = null

    fun load(): Boolean {
        val success = selectNewFiles(FileChooserMode.Single)
        if (!success) return false

        model.item = ClassicTalentsSerializer.load(currentDataFile!!, currentLangFile!!)
        return true
    }

    fun save(toNewFile: Boolean = false): Boolean {
        val bothFilesExist = (currentDataFile?.exists() ?: false) && (currentLangFile?.exists() ?: false)
        if (toNewFile || !bothFilesExist) {
            val success = selectNewFiles(FileChooserMode.Save)
            if (!success) return false
        }

        ClassicTalentsSerializer.save(model.item, currentDataFile!!, currentLangFile!!)
        return true
    }

    fun quit() {
        Platform.exit()
        exitProcess(0)
    }

    private fun selectNewFiles(mode: FileChooserMode): Boolean {
        OUTPUT_DIR.mkdirs()
        val dataFiles = chooseFile("Choose a data file", arrayOf(JSON_FILE_FILTER, ALL_FILES_FILTER), OUTPUT_DIR, mode)
        currentDataFile = dataFiles.firstOrNull() ?: return false
        val langFiles = chooseFile("Choose a language file", arrayOf(PROPERTIES_FILE_FILTER, ALL_FILES_FILTER), OUTPUT_DIR, mode)
        currentLangFile = langFiles.firstOrNull() ?: return false

        return true
    }
}