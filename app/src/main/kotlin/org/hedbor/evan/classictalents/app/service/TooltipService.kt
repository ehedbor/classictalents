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

package org.hedbor.evan.classictalents.app.service

import javafx.scene.control.Tooltip
import javafx.util.Duration


object TooltipService {
    /**
     * Globally modify the behavior of all tooltips. No, there isn't a way to do this on a per-tooltip basis without
     * reinventing the wheel (at least until Java 9).
     *
     * @return `true` on success
     */
    fun setGlobalTooltipBehavior(
        open: Duration = Duration(1000.0),
        visible: Duration = Duration(5000.0),
        close: Duration = Duration(200.0),
        hideOnExit: Boolean = false
    ): Boolean {
        try {
            val tooltipBehaviorClass = Tooltip::class.java.declaredClasses.first { it.name == "javafx.scene.control.Tooltip\$TooltipBehavior" }
            val constructor = tooltipBehaviorClass.getDeclaredConstructor(
                Duration::class.java,
                Duration::class.java,
                Duration::class.java,
                Boolean::class.javaPrimitiveType)
            constructor.isAccessible = true
            val tooltipBehavior = constructor.newInstance(open, visible, close, hideOnExit)

            val behaviorField = Tooltip::class.java.getDeclaredField("BEHAVIOR")
            behaviorField.isAccessible = true
            behaviorField.set(null, tooltipBehavior)

            // clean up
            constructor.isAccessible = false
            behaviorField.isAccessible = false

            return true
        } catch (e: Exception) {
            return false
        }
    }
}