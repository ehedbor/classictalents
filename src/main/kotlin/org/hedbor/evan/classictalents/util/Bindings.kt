package org.hedbor.evan.classictalents.util

import javafx.beans.Observable
import javafx.beans.binding.*
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet

fun <T> objectBinding(vararg dependencies: Observable, computeValue: () -> T): ObjectBinding<T> {
    return object : ObjectBinding<T>() {
        init { bind(*dependencies) }
        override fun computeValue() = computeValue()
    }
}

fun <T, R, V> R.objectBinding(vararg dependencies: Observable, computeValue: (V) -> T): ObjectBinding<T>
    where R : ObservableValue<V> {
    return object : ObjectBinding<T>() {
        init { bind(this@objectBinding, *dependencies) }
        override fun computeValue() = computeValue(this@objectBinding.value)
    }
}

fun booleanBinding(vararg dependencies: Observable, computeValue: () -> Boolean): BooleanBinding {
    return object : BooleanBinding() {
        init { bind(*dependencies) }
        override fun computeValue() = computeValue()
    }
}

fun <R, V> R.booleanBinding(vararg dependencies: Observable, computeValue: (V) -> Boolean): BooleanBinding
    where R : ObservableValue<V> {
    return object : BooleanBinding() {
        init { bind(this@booleanBinding, *dependencies) }
        override fun computeValue() = computeValue(this@booleanBinding.value)
    }
}

fun doubleBinding(vararg dependencies: Observable, computeValue: () -> Double): DoubleBinding {
    return object : DoubleBinding() {
        init { bind(*dependencies) }
        override fun computeValue() = computeValue()
    }
}

fun <R, V> R.doubleBinding(vararg dependencies: Observable, computeValue: (V) -> Double): DoubleBinding
    where R : ObservableValue<V> {
    return object : DoubleBinding() {
        init { bind(this@doubleBinding, *dependencies) }
        override fun computeValue() = computeValue(this@doubleBinding.value)
    }
}

fun floatBinding(vararg dependencies: Observable, computeValue: () -> Float): FloatBinding {
    return object : FloatBinding() {
        init { bind(*dependencies) }
        override fun computeValue() = computeValue()
    }
}

fun <R, V> R.floatBinding(vararg dependencies: Observable, computeValue: (V) -> Float): FloatBinding
    where R : ObservableValue<V> {
    return object : FloatBinding() {
        init { bind(this@floatBinding, *dependencies) }
        override fun computeValue() = computeValue(this@floatBinding.value)
    }
}

fun intBinding(vararg dependencies: Observable, computeValue: () -> Int): IntegerBinding {
    return object : IntegerBinding() {
        init { bind(*dependencies) }
        override fun computeValue() = computeValue()
    }
}

fun <R, V> R.intBinding(vararg dependencies: Observable, computeValue: (V) -> Int): IntegerBinding
    where R : ObservableValue<V> {
    return object : IntegerBinding() {
        init { bind(this@intBinding, *dependencies) }
        override fun computeValue() = computeValue(this@intBinding.value)
    }
}

fun longBinding(vararg dependencies: Observable, computeValue: () -> Long): LongBinding {
    return object : LongBinding() {
        init { bind(*dependencies) }
        override fun computeValue() = computeValue()
    }
}

fun <R, V> R.longBinding(vararg dependencies: Observable, computeValue: (V) -> Long): LongBinding
    where R : ObservableValue<V> {
    return object : LongBinding() {
        init { bind(this@longBinding, *dependencies) }
        override fun computeValue() = computeValue(this@longBinding.value)
    }
}

fun stringBinding(vararg dependencies: Observable, computeValue: () -> String?): StringBinding {
    return object : StringBinding() {
        init { bind(*dependencies) }
        override fun computeValue() = computeValue()
    }
}

fun <R, V> R.stringBinding(vararg dependencies: Observable, computeValue: (V) -> String?): StringBinding
    where R : ObservableValue<V> {
    return object : StringBinding() {
        init { bind(this@stringBinding, *dependencies) }
        override fun computeValue() = computeValue(this@stringBinding.value)
    }
}

fun <E> listBinding(vararg dependencies: Observable, computeValue: () -> ObservableList<E>?): ListBinding<E> {
    return object : ListBinding<E>() {
        init { bind(*dependencies) }
        override fun computeValue() = computeValue()
    }
}

fun <R, V, E> R.listBinding(vararg dependencies: Observable, computeValue: (V) -> ObservableList<E>?): ListBinding<E>
    where R : ObservableValue<V> {
    return object : ListBinding<E>() {
        init { bind(this@listBinding, *dependencies) }
        override fun computeValue() = computeValue(this@listBinding.value)
    }
}

fun <K, V> mapBinding(vararg dependencies: Observable, computeValue: () -> ObservableMap<K, V>?): MapBinding<K, V> {
    return object : MapBinding<K, V>() {
        init { bind(*dependencies) }
        override fun computeValue() = computeValue()
    }
}

fun <R, RV, K, V> R.mapBinding(vararg dependencies: Observable, computeValue: (RV) -> ObservableMap<K, V>?): MapBinding<K, V>
    where R : ObservableValue<RV> {
    return object : MapBinding<K, V>() {
        init { bind(this@mapBinding, *dependencies) }
        override fun computeValue() = computeValue(this@mapBinding.value)
    }
}

fun <E> setBinding(vararg dependencies: Observable, computeValue: () -> ObservableSet<E>?): SetBinding<E> {
    return object : SetBinding<E>() {
        init { bind(*dependencies) }
        override fun computeValue() = computeValue()
    }
}

fun <R, V, E> R.setBinding(vararg dependencies: Observable, computeValue: (V) -> ObservableSet<E>?): SetBinding<E>
    where R : ObservableValue<V> {
    return object : SetBinding<E>() {
        init { bind(this@setBinding, *dependencies) }
        override fun computeValue() = computeValue(this@setBinding.value)
    }
}