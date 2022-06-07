package org.hedbor.evan.classictalents.service

import org.hedbor.evan.classictalents.model.WowClass

object YamlService {
    fun loadClass(filePath: String): WowClass {
        val stream = javaClass.getResourceAsStream(filePath)
        TODO("implement a YML reader")
    }
}