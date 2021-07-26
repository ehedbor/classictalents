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