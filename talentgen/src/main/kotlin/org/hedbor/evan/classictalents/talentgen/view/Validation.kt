/*
 * WoW Classic Talent Calculator
 * Copyright (C) 2020-2022 Evan Hedbor
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.hedbor.evan.classictalents.talentgen.view

import javafx.scene.control.TextInputControl
import tornadofx.ValidationContext
import tornadofx.ValidationMessage
import tornadofx.validator
import kotlin.reflect.KClass


/**
 * The default [tornadofx.required] method returns null when validation is successful.
 * This method returns [tornadofx.ValidationContext.success] in such a situation.
 */
internal fun TextInputControl.mustBePresent() {
    validator {
        if (text.isNullOrBlank()) error("This field is required.") else success()
    }
}

internal fun TextInputControl.mustBeValidKey() {
    validator {
        val text = text
        if (text.isNullOrBlank()) {
            null
        } else if (!text.matches("^[0-9a-z_]+$".toRegex())) {
            error("Translation keys may only contain lowercase ASCII letters, numbers and underscores.")
        } else {
            success()
        }
    }
}

/**
 * Validator function that returns [tornadofx.ValidationContext.error] when the
 * input is not within the specified range.
 *
 * Does not require that the input is present.
 */
internal fun TextInputControl.mustBeInRange(range: IntRange) {
    validator {
        mustBeInRange(this@mustBeInRange, range)
    }
}

/**
 * Validator function that returns [tornadofx.ValidationContext.error] when the
 * input is not within the specified range.
 *
 * Properties that are not directly part of a [tornadofx.ViewModel] must be added
 * with this function or the program will crash.
 */
internal fun ValidationContext.mustBeInRange(control: TextInputControl, range: IntRange): ValidationMessage? {
    val text = control.text
    return if (text.isNullOrBlank()) {
        null
    } else {
        val quantity = text.toIntOrNull()
        if (quantity == null) {
            error("Field is not an integer.")
        } else {
            if (quantity in range) success() else error("Must be within the range $range.")
        }
    }
}

/**
 * Validator function that returns [tornadofx.ValidationContext.success] when
 * the input is a non-negative number.
 *
 * Does not require that the input is present.
 *
 * Only supports [Int] or [Double] since i cant be bothered to add more than
 * those and i'll never use other number types anyways. Sue me.
 */
internal inline fun <reified T : Number> TextInputControl.mustBeNonNegative(allowZero: Boolean = true) {
    validator { mustBeNonNegative(this@mustBeNonNegative, T::class, allowZero) }
}

/**
 * Validator function that returns [tornadofx.ValidationContext.success] when
 * the input is a non-negative number.
 *
 * Properties that are not directly part of a [tornadofx.ViewModel] must be added
 * with this function or the program will crash.
 */
internal inline fun <reified T : Number> ValidationContext.mustBeNonNegative(
    control: TextInputControl,
    allowZero: Boolean = true
) {
    this.mustBeNonNegative(control, T::class, allowZero)
}

private fun <T : Number> ValidationContext.mustBeNonNegative(
    control: TextInputControl,
    clazz: KClass<T>,
    allowZero: Boolean
): ValidationMessage? {
    val text = control.text
    return if (text.isNullOrBlank()) {
        null
    } else when (clazz) {
        Int::class -> {
            val quantity = text.toIntOrNull()
            if (quantity == null) {
                error("Field is not an integer.")
            } else {
                if (allowZero && quantity < 0)
                    error("Must be a non-negative integer.")
                else if (!allowZero && quantity <= 0)
                    error("Must be a positive integer.")
                else success()
            }
        }
        Double::class -> {
            val quantity = text.toDoubleOrNull()
            if (quantity == null) {
                error("Field is not a real number.")
            } else {
                if (allowZero && quantity < 0.0)
                    error("Must be a non-negative real number.")
                else if (!allowZero && quantity <= 0.0)
                    error("Must be a positive real number.")
                else success()
            }
        }
        else -> {
            throw IllegalArgumentException("Unexpected number type '${clazz.qualifiedName}'")
        }
    }
}