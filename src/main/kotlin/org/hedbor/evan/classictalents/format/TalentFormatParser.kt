package org.hedbor.evan.classictalents.format

internal class TalentFormatParser(private val pattern: String, private var nextFormatArgIndex: Int? = 0) {
    private var index = 0

    private fun peek() = pattern.getOrNull(index)
    private fun next() = pattern.getOrNull(index++)

    fun parse(): List<TextComponent> {
        val result = mutableListOf<TextComponent>()
        var rawText = ""
        while (peek() != null) {
            when (val char = next()) {
                '\\' -> {
                    rawText += parseEscape()
                }
                '{' -> {
                    if (rawText.isNotEmpty()) {
                        result += TextComponent.Raw(rawText)
                        rawText = ""
                    }
                    result += parseFormat()
                }
                else -> {
                    rawText += char
                }
            }
        }
        if (rawText.isNotEmpty()) {
            result += TextComponent.Raw(rawText)
        }
        return result
    }

    private fun parseEscape(): String {
        val escape = if (peek() != null) next()!! else throw FormatException("Escape sequence at end of pattern")
        return "" + when (escape) {
            in "\\{}," -> escape
            'r' -> '\r'
            'n' -> '\n'
            't' -> '\t'
            '0' -> '\u0000'
            else -> throw FormatException("Bad escape sequence '\\$escape'")
        }
    }

    private fun parseFormat(): TextComponent {
        var formatArgIndex = parseInt()
        if (formatArgIndex == null) {
            if (nextFormatArgIndex == null) {
                throw FormatException("Unable to determine format arg index (implicit indices are forbidden after the first explicit index)")
            }
            formatArgIndex = nextFormatArgIndex!!
            nextFormatArgIndex = nextFormatArgIndex!! + 1
        } else {
            // forbid implicit arg indices
            nextFormatArgIndex = null
        }

        return when (val separator = next()) {
            '}' -> TextComponent.Format.Substitution(formatArgIndex)
            ':' -> when (val opt = next()) {
                '}' -> TextComponent.Format.Substitution(formatArgIndex)
                '|' -> parseSimpleChoiceFormat(formatArgIndex)
                '?' -> parseComplexChoiceFormat(formatArgIndex)
                else -> throw FormatException("Unknown format option '$opt'")
            }
            else -> throw FormatException("Unknown format separator '$separator' (expected '}' or ':')")
        }
    }

    private fun parseSimpleChoiceFormat(formatArgIndex: Int): TextComponent.Format.Simple {
        val subcomponents = mutableListOf<List<TextComponent>>()

        var foundEndBrace = false
        while (!foundEndBrace) {
            val start = index
            foundEndBrace = findSubpatternEnd()
            subcomponents += parseSubpattern(start)
        }

        return TextComponent.Format.Simple(formatArgIndex, subcomponents)
    }

    private fun parseComplexChoiceFormat(formatArgIndex: Int): TextComponent.Format.Complex {
        val choices = mutableListOf<TextComponent.Format.Complex.Choice>()

        var foundCloseBrace = false
        while (!foundCloseBrace) {
            // first, parse the operator
            val op: String = parseCompareOp()

            // then, get the value
            val value = if (op == "_") {
                Double.NaN
            } else {
                parseDouble() ?: throw FormatException("Expected float in choice format after operator")
            }

            // make sure there is a colon after the value
            val maybeColon = next()
            if (maybeColon != ':') {
                throw FormatException("Expected ':' in choice format, got '$maybeColon'")
            }

            // finally, parse the subpattern
            val start = index
            foundCloseBrace = findSubpatternEnd()
            val subcomponents = parseSubpattern(start)

            choices += TextComponent.Format.Complex.Choice(op, value, subcomponents)
        }

        return TextComponent.Format.Complex(formatArgIndex, choices)
    }

    /**
     * Advances the parser until the end of the subpattern is found
     *
     * @return true if it is also the end of the choice format
     */
    private fun findSubpatternEnd(): Boolean {
        var foundCloseBrace = false
        var foundSubpatternEnd = false
        var nestingLevel = 0
        while (!foundSubpatternEnd) {
            when (next()) {
                '\\' -> {
                    // escape sequence, ignore the next character
                    next()
                }
                '{' -> {
                    nestingLevel++
                }
                ',' -> {
                    if (nestingLevel == 0) {
                        foundSubpatternEnd = true
                    }
                }
                '}' -> {
                    if (nestingLevel == 0) {
                        foundSubpatternEnd = true
                        foundCloseBrace = true
                    } else {
                        nestingLevel--
                    }
                }
                null -> throw FormatException("Choice format ended unexpectedly")
                else -> {
                    // part of subpattern, ignore
                }
            }
        }
        return foundCloseBrace
    }

    private fun parseSubpattern(start: Int): List<TextComponent> {
        val subpattern = pattern.substring(start until index - 1)
        val subparser = TalentFormatParser(subpattern, nextFormatArgIndex)
        val subcomponents = subparser.parse()
        this.nextFormatArgIndex = subparser.nextFormatArgIndex

        return subcomponents
    }

    private fun parseCompareOp(): String {
        return when (val op = next()) {
            '>' -> {
                if (peek() == '=') {
                    next()
                    ">="
                } else {
                    ">"
                }
            }
            '<' -> {
                if (peek() == '=') {
                    next()
                    "<="
                } else {
                    "<"
                }
            }
            '=' -> "="
            '_' -> "_"
            else -> {
                throw FormatException("Expected choice operator (one of > < >= <= = != _), got '$op'")
            }
        }
    }

    private fun parseInt(): Int? {
        val startPos = index
        while (peek() in '0'..'9') {
            next()
        }

        return if (startPos == index) {
            null
        } else {
            pattern.substring(startPos until index).toInt()
        }
    }

    private fun parseDouble(): Double? {
        val startPos = index
        while (peek() in '0'..'9') {
            next()
        }
        if (startPos == index) return null

        if (peek() == '.') {
            next()

            val decimalStartPos = index
            while (peek() in '0'..'9') {
                next()
            }
            if (decimalStartPos == index) {
                throw FormatException("Expected decimal in float value, got nothing")
            }
        }

        return pattern.substring(startPos until index).toDouble()
    }
}