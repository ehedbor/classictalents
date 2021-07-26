package org.hedbor.evan.classictalents.app.util

import javafx.beans.binding.IntegerExpression
import javafx.beans.property.Property
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import tornadofx.ChangeListener
import tornadofx.onChange


/**
 * @see tornadofx.selectBoolean
 */
fun <T> ObservableValue<T>.selectInteger(nested: (T) -> IntegerExpression): IntegerExpression {
    fun extractNested() = nested(value)

    val dis = this
    var currentNested = extractNested()

    return object : SimpleIntegerProperty() {
        val changeListener = ChangeListener<Number> { _, _, _ ->
            currentNested = extractNested()
            fireValueChangedEvent()
        }

        init {
            dis.onChange {
                fireValueChangedEvent()
                invalidated()
            }
        }

        override fun invalidated() {
            currentNested.removeListener(changeListener)
            currentNested = extractNested()
            currentNested.addListener(changeListener)
        }

        override fun get() = currentNested.value

        override fun set(v: Int) {
            (currentNested as? WritableValue<*>)?.value = v
            super.setValue(v)
        }
    }
}

fun <T : Any, N : Any, TProp : Property<T?>> TProp.bindWhenNotNull(
    nested: ObservableValue<out N?>,
    defaultValue: T? = null,
    block: (N) -> ObservableValue<out T?>
): TProp {

    val initialValue = nested.value
    if (initialValue == null) {
        value = defaultValue
    } else {
        bind(block(initialValue))
    }

    nested.addListener { _, _, newValue ->
        if (newValue == null) {
            unbind()
            value = defaultValue
        } else {
            bind(block(newValue))
        }
    }
    return this
}