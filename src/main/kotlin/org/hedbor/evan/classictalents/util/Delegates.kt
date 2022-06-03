package org.hedbor.evan.classictalents.util

import javafx.beans.property.*
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import org.hedbor.evan.classictalents.model.Spell
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

// See tornadofx for original implementation:
// https://github.com/edvin/tornadofx/blob/master/src/main/java/tornadofx/Properties.kt

inline fun <reified T> property(value: T? = null): PropertyDelegate<T> {
    val prop: Property<*> = when (T::class) {
        Boolean::class -> SimpleBooleanProperty((value ?: false) as Boolean)
        Double::class -> SimpleDoubleProperty((value ?: 0.0) as Double)
        Float::class -> SimpleFloatProperty((value ?: 0.0f) as Float)
        Int::class -> SimpleIntegerProperty((value ?: 0) as Int)
        Long::class -> SimpleLongProperty((value ?: 0L) as Long)
        String::class -> SimpleStringProperty(value as String?)
        ObservableList::class -> SimpleListProperty(value as ObservableList<*>?)
        ObservableMap::class -> SimpleMapProperty(value as ObservableMap<*, *>?)
        ObservableSet::class -> SimpleSetProperty(value as ObservableSet<*>?)
        else -> SimpleObjectProperty(value)
    }
    @Suppress("UNCHECKED_CAST")
    return PropertyDelegate(prop as Property<T>)
}

fun <T> property(block: () -> Property<T>) = PropertyDelegate(block())

class PropertyDelegate<T>(internal val javafxProperty : Property<T>) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return javafxProperty.value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        javafxProperty.value = value
    }
}

private fun <T> Any.getDelegate(prop: KMutableProperty1<*, T>): PropertyDelegate<T> {
    // avoid kotlin-reflect dependency
    val field = requireNotNull(javaClass.getField("${prop.name}\$delegate")) { "No delegate field with name '${prop.name}' found" }

    field.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    return field.get(this) as PropertyDelegate<T>
}

fun <T> Any.getProperty(prop: KMutableProperty1<*, T>) = getDelegate(prop).javafxProperty as ObjectProperty<T>

fun Any.getProperty(prop: KMutableProperty1<*, Boolean>) = getDelegate(prop).javafxProperty as BooleanProperty
fun Any.getProperty(prop: KMutableProperty1<*, Double>) = getDelegate(prop).javafxProperty as DoubleProperty
fun Any.getProperty(prop: KMutableProperty1<*, Float>) = getDelegate(prop).javafxProperty as FloatProperty
fun Any.getProperty(prop: KMutableProperty1<*, Int>) = getDelegate(prop).javafxProperty as IntegerProperty
fun Any.getProperty(prop: KMutableProperty1<*, String>) = getDelegate(prop).javafxProperty as StringProperty

@Suppress("UNCHECKED_CAST")
fun <E> Any.getProperty(prop: KMutableProperty1<Spell, ObservableList<String>>) = getDelegate(prop).javafxProperty as ListProperty<E>
@Suppress("UNCHECKED_CAST")
fun <K, V> Any.getProperty(prop: KMutableProperty1<*, Map<K, V>>) = getDelegate(prop).javafxProperty as MapProperty<K, V>
@Suppress("UNCHECKED_CAST")
fun <E> Any.getProperty(prop: KMutableProperty1<*, Set<E>>) = getDelegate(prop).javafxProperty as SetProperty<E>
