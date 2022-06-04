package org.hedbor.evan.classictalents.util

import javafx.beans.Observable
import javafx.beans.binding.DoubleBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ObservableValue

fun <T> objectBinding(vararg dependencies: Observable, computeValue: () -> T): ObjectBinding<T> {
    return object : ObjectBinding<T>() {
        init {
            bind(*dependencies)
        }
        override fun computeValue(): T {
            return computeValue()
        }
    }
}

fun <T, R, V> R.objectBinding(vararg dependencies: Observable, computeValue: (V) -> T): ObjectBinding<T>
    where R : ObservableValue<V> {
    return object : ObjectBinding<T>() {
        init {
            bind(this@objectBinding, *dependencies)
        }
        override fun computeValue(): T {
            return computeValue(this@objectBinding.value)
        }
    }
}

fun doubleBinding(vararg dependencies: Observable, computeValue: () -> Double): DoubleBinding {
    return object : DoubleBinding() {
        init {
            bind(*dependencies)
        }
        override fun computeValue(): Double {
            return computeValue()
        }
    }
}

fun <R, V> R.doubleBinding(vararg dependencies: Observable, computeValue: (V) -> Double): DoubleBinding
    where R : ObservableValue<V> {
    return object : DoubleBinding() {
        init {
            bind(this@doubleBinding, *dependencies)
        }
        override fun computeValue(): Double {
            return computeValue(this@doubleBinding.value)
        }
    }
}

// TODO: add bindings for other types