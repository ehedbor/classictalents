package org.hedbor.evan.classictalents.format


/**
 * A formatter specialized in formatting talent descriptions.
 *
 * ## Format patterns
 *
 * A format pattern consists of an opening `"{"`, an (optional) index into the args array,
 * the format args, and a closing `"}"`.
 *
 * If the arg index is not present, it will be determined automatically
 * (For instance, `"{}{}{}"` refers to the same pattern as `"{0}{1}{2}"`).
 * The implicit index is only allowed before any explicit indices have occurred
 * (e.g. `"{1}{}"` is forbidden).
 *
 * There are three types of format patterns:
 * 1. Simple substitutions (Ex: `"{}"`, `"{:}"` `"{4}"`)
 * 2. Talent choices (Ex: `"{:|A,B,C}"`, `"{0:|1 point,2 points,3 points,4 points,5 points}"`).
 *     The format arg is used to determine which subpattern will be shown, where the leftmost is pattern #1.
 * 3. Generalized choices (Ex: `"{:?=1:cat,_:cats}"`). This compares the format arg (as a double) to
 *     the given value and results in the first branch for which the comparison is `true`.
 *     The allowed operators are "`>`", "`>=`", "`<`", "`<=`", "`=`" and "`!=`".
 *     Additionally, "`_`" can be used as a wildcard.
 *
 * Patterns may be nested. For example, `TalentFormatter("{:|A={},B={}}").format(2, "hi", "there")`
 * yields `"B=there"`.
 *
 * ## Grammar:
 *
 * The grammar uses the following symbols:
 *  * an identifier denotes a rule
 *  * `::=` defines a rule
 *  * `*` denotes repetition (zero or more)
 *  * `+` denotes repetition (one or more)
 *  * `?` denotes optional elements (zero or one)
 *  * `|` denotes a choice between 2 or more options
 *  * `(...)` denotes a group
 *  * `"..."` denotes literal text
 *  * `/.../` denotes a regular expression
 *
 * Here is a complete description of the grammar:
 * ```
 * text ::= component*
 * component ::= format
 *             | rawText
 *
 * format ::= "{" int? choice? "}"
 * choice ::= ":" ( simpleChoice | complexChoice )?
 *
 * simpleChoice ::= "|" text ( "," text )*
 *
 * complexChoice ::= "?" complexChoiceBranch ( "," complexChoiceBranch )*
 * complexChoiceBranch ::= test ":" text
 * complexChoiceTest ::= operator float
 *                     | "_"
 * complexChoiceOperator ::= ">" | ">="
 *                         | "<" | "<="
 *                         | "=" | "!="
 *
 * rawText ::= ( escapeSequence | charSequence )+
 * escapeSequence ::= /\\[\\{},rnt0]/
 * charSequence ::= /.+/
 *
 * int ::= /[0-9]+/
 * float ::= /[0-9]+(\.[0-9]+)?/
 * ```
 */
class TalentFormatter(pattern: String) {
    private val components = TalentFormatParser(pattern).parse()

    fun format(vararg args: Any?): String {
        return components.joinToString(separator = "") { formatComponent(it, args) }
    }

    private fun formatComponent(component: TextComponent, args: Array<out Any?>): String {
        return when (component) {
            is TextComponent.Raw -> component.text
            is TextComponent.Format -> {
                if (component.argIndex >= args.size) {
                    throw FormatException("Argument index ${component.argIndex} too high for choice format (${args.size} args present)")
                } else if (component.argIndex < 0) {
                    throw FormatException("Argument index must be positive (got ${component.argIndex})")
                }
           
                when (component) {
                    is TextComponent.Format.Substitution -> {
                        args[component.argIndex].toString()
                    }
                    is TextComponent.Format.Simple -> formatSimple(component, args)
                    is TextComponent.Format.Complex -> formatComplex(component, args)

                }
            }
        }
    }

    private fun formatSimple(component: TextComponent.Format.Simple, args: Array<out Any?>): String {
        var i = args[component.argIndex]
        if (i !is Int) {
            val name = i?.let { it::class.simpleName.toString() } ?: "null"
            throw FormatException("Expected int for choice index, got $name")
        }
        // talents are 1-indexed, not 0-indexed
        i -= 1
        if (i > component.choices.size) {
            throw FormatException("Index $i too high for choice format (last index=${component.choices.lastIndex})")
        } else if (i < 0) {
            throw FormatException("Index $i too low for choice format (first index=0)")
        }
        return component.choices[i].joinToString(separator = "") { formatComponent(it, args) }
    }

    private fun formatComplex(component: TextComponent.Format.Complex, args: Array<out Any?>): String {
        var value = args[component.argIndex]
        if (value !is Number) {
            val name = value?.let { it::class.simpleName.toString() } ?: "null"
            throw FormatException("Expected number for choice value, got $name")
        }
        value = value.toDouble()

        val selectedChoice = component.choices.firstOrNull { choice ->
            when (choice.op) {
                "=" -> value == choice.value
                "!=" -> value != choice.value
                ">" -> value > choice.value
                ">=" -> value >= choice.value
                "<" -> value < choice.value
                "<=" -> value <= choice.value
                "_" -> true
                else -> throw IllegalStateException("Unknown choice operator ${choice.op}")
            }
        }
        return selectedChoice?.text?.joinToString { formatComponent(it, args) } ?: ""
    }
}