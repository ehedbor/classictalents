package org.hedbor.evan.classictalents.util

import javafx.scene.control.Tooltip
import javafx.util.Duration


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