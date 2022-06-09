@file:Suppress("unused")

package org.hedbor.evan.classictalents.util

import javafx.collections.*

fun <E> observableListOf(vararg items: E): ObservableList<E> =
    FXCollections.observableArrayList(*items)

fun <K, V> observableMapOf(vararg entries: Pair<K, V>): ObservableMap<K, V> =
    FXCollections.observableHashMap<K, V>().also { it.putAll(entries) }

fun <E> observableSetOf(vararg items: E): ObservableSet<E> =
    FXCollections.observableSet(*items)

fun <E> emptyObservableList(): ObservableList<E> = FXCollections.emptyObservableList()

fun <K, V> emptyObservableMap(): ObservableMap<K, V> = FXCollections.emptyObservableMap()

fun <E> emptyObservableSet(): ObservableSet<E> = FXCollections.emptyObservableSet()

fun <E> Iterable<E>.toObservableList(): ObservableList<E> =
    observableListOf<E>().also { it.addAll(this) }

fun <K, V> Map<K, V>.toObservableMap(): ObservableMap<K, V> =
    observableMapOf<K, V>().also { it.putAll(this) }

fun <K, V> Iterable<Pair<K, V>>.toObservableMap(): ObservableMap<K, V> =
    observableMapOf<K, V>().also { it.putAll(this) }

fun <E> Iterable<E>.toObservableSet(): ObservableSet<E> =
    observableSetOf<E>().also { it.addAll(this) }