package org.hedbor.evan.classictalents.common.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hedbor.evan.classictalents.common.model.WowClass
import java.io.File


object ClassicTalentsSerializer {
    @OptIn(ExperimentalSerializationApi::class)
    private val jsonParser by lazy {
        Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }
    }

    fun load(dataFile: File, languageFile: File): WowClass {
        val wowClass = jsonParser.decodeFromString<WowClass>(dataFile.readText())
        Translations.load(wowClass, languageFile)
        return wowClass
    }

    fun save(wowClass: WowClass, dataFile: File, languageFile: File) {
        dataFile.writeText(jsonParser.encodeToString(wowClass))
        Translations.save(wowClass, languageFile)
    }

}