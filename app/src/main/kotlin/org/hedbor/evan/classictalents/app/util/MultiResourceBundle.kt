package org.hedbor.evan.classictalents.app.util

import java.util.*


class MultiResourceBundle(private val delegates: List<ResourceBundle> = emptyList()) : ResourceBundle() {
    class Control(private val delegateBaseNames: Array<out String>) : ResourceBundle.Control() {
        override fun newBundle(
            baseName: String,
            locale: Locale,
            format: String,
            loader: ClassLoader,
            reload: Boolean
        ): ResourceBundle {
            val delegates = delegateBaseNames
                .filter { it.isNotBlank() }
                .map { getBundle(it, locale, loader) }
            return MultiResourceBundle(delegates)
        }
    }

    override fun handleGetObject(key: String): Any? {
        val obj = delegates
            .filter { it.containsKey(key) }
            .map { it.getObject(key) }
            .firstOrNull()
        return obj
    }

    override fun getKeys(): Enumeration<String> {
        val keys = delegates.flatMap { it.keys.toList() }
        return Collections.enumeration(keys)
    }
}