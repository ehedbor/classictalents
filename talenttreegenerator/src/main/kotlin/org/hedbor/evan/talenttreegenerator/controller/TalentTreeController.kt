package org.hedbor.evan.talenttreegenerator.controller

import javafx.application.Platform
import javafx.stage.FileChooser
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hedbor.evan.talenttreegenerator.model.WowClass
import org.hedbor.evan.talenttreegenerator.model.serializers.TranslationsSerializer
import tornadofx.Controller
import tornadofx.FileChooserMode
import tornadofx.chooseFile
import java.io.File
import kotlin.system.exitProcess


class TalentTreeController : Controller() {
    companion object {
        private val OUTPUT_DIR = File("./generated-talents")
        private val JSON_FILE_FILTER = FileChooser.ExtensionFilter("Json Files", ".json")
        private val PROPERTIES_FILE_FILTER = FileChooser.ExtensionFilter("Java Properties Files", ".properties")
    }

    val wowClass = WowClass(displayName = "New Class")

    private var currentDataFile: File? = null
    private var currentLangFile: File? = null

    @OptIn(ExperimentalSerializationApi::class)
    private val preferredJsonParser by lazy {
        Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }
    }

    fun load(): Boolean {
        val success = selectNewFiles()
        if (!success) return false

        val newWowClass = preferredJsonParser.decodeFromString<WowClass>(currentDataFile!!.readText())
        TranslationsSerializer.read(newWowClass, currentLangFile!!)
        
        return true
    }

    fun save(toNewFile: Boolean = false): Boolean {
        val bothFilesExist = (currentDataFile?.exists() ?: false) && (currentLangFile?.exists() ?: false)
        if (toNewFile || !bothFilesExist) {
            val success = selectNewFiles()
            if (!success) return false
        }

        currentDataFile!!.writeText(preferredJsonParser.encodeToString(wowClass))
        TranslationsSerializer.write(wowClass, currentLangFile!!)

        return true
    }

    fun quit() {
        Platform.exit()
        exitProcess(0)
    }

    private fun selectNewFiles(): Boolean {
        OUTPUT_DIR.mkdirs()
        val dataFiles = chooseFile("Choose a data file", arrayOf(JSON_FILE_FILTER), OUTPUT_DIR, FileChooserMode.Save)
        currentDataFile = dataFiles.firstOrNull() ?: return false
        val langFiles = chooseFile("Choose a language file", arrayOf(PROPERTIES_FILE_FILTER), OUTPUT_DIR, FileChooserMode.Save)
        currentLangFile = langFiles.firstOrNull() ?: return false

        return true
    }


}