@file:Suppress("unused")

package org.hedbor.evan.classictalents.util

import javafx.beans.property.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


fun <C, T> Property<T>.delegate() = PropDelegate<C, T>(this)

// special cases for non-nullable properties
fun <C> BooleanProperty.delegate() = BoolPropDelegate<C>(this)
fun <C> IntegerProperty.delegate() = IntPropDelegate<C>(this)
fun <C> LongProperty.delegate() = LongPropDelegate<C>(this)
fun <C> FloatProperty.delegate() = FloatPropDelegate<C>(this)
fun <C> DoubleProperty.delegate() = DoublePropDelegate<C>(this)


class PropDelegate<in C, T>(private val fxProp: Property<T>): ReadWriteProperty<C, T> {
    override operator fun getValue(thisRef: C, property: KProperty<*>): T {
        return fxProp.value
    }

    override operator fun setValue(thisRef: C, property: KProperty<*>, value: T) {
        fxProp.value = value
    }
}

class BoolPropDelegate<in C>(private val fxProp: BooleanProperty): ReadWriteProperty<C, Boolean> {
    override operator fun getValue(thisRef: C, property: KProperty<*>): Boolean {
        return fxProp.value
    }

    override operator fun setValue(thisRef: C, property: KProperty<*>, value: Boolean) {
        fxProp.value = value
    }
}

class IntPropDelegate<in C>(private val fxProp: IntegerProperty): ReadWriteProperty<C, Int> {
    override operator fun getValue(thisRef: C, property: KProperty<*>): Int {
        return fxProp.value
    }

    override operator fun setValue(thisRef: C, property: KProperty<*>, value: Int) {
        fxProp.value = value
    }
}

class LongPropDelegate<in C>(private val fxProp: LongProperty): ReadWriteProperty<C, Long> {
    override operator fun getValue(thisRef: C, property: KProperty<*>): Long {
        return fxProp.value
    }

    override operator fun setValue(thisRef: C, property: KProperty<*>, value: Long) {
        fxProp.value = value
    }
}

class FloatPropDelegate<in C>(private val fxProp: FloatProperty): ReadWriteProperty<C, Float> {
    override operator fun getValue(thisRef: C, property: KProperty<*>): Float {
        return fxProp.value
    }

    override operator fun setValue(thisRef: C, property: KProperty<*>, value: Float) {
        fxProp.value = value
    }
}

class DoublePropDelegate<in C>(private val fxProp: DoubleProperty): ReadWriteProperty<C, Double> {
    override operator fun getValue(thisRef: C, property: KProperty<*>): Double {
        return fxProp.value
    }

    override operator fun setValue(thisRef: C, property: KProperty<*>, value: Double) {
        fxProp.value = value
    }
}