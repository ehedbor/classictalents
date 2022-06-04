@file:Suppress("unused")

package org.hedbor.evan.classictalents.util

import javafx.collections.*

fun <E> observableListOf(vararg items: E): ObservableList<E> =
    FXCollections.observableArrayList(*items)

fun <K, V> observableMapOf(vararg entries: Pair<K, V>): ObservableMap<K, V> =
    FXCollections.observableHashMap<K, V>().also { it.putAll(entries) }

fun <E> observableSetOf(vararg items: E): ObservableSet<E> =
    FXCollections.observableSet(*items)

fun <E> emptyObservableList(mutable: Boolean = true): ObservableList<E> =
    if (mutable) observableListOf() else FXCollections.emptyObservableList()

fun <K, V> emptyObservableMap(mutable: Boolean = true): ObservableMap<K, V> =
    if (mutable) observableMapOf() else FXCollections.emptyObservableMap()

fun <E> emptyObservableSet(mutable: Boolean = true): ObservableSet<E> =
    if (mutable) observableSetOf() else FXCollections.emptyObservableSet()