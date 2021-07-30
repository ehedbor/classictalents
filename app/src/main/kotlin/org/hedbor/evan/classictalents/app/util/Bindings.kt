/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2021 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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