package org.hedbor.evan.classictalents.format

internal sealed class TextComponent {
    class Raw(val text: String) : TextComponent()

    sealed class Format(val argIndex: Int) : TextComponent() {
        class Substitution(argIndex: Int) : Format(argIndex)

        class Simple(argIndex: Int, val choices: List<List<TextComponent>>) : Format(argIndex)

        class Complex(argIndex: Int, val choices: List<Choice>) : Format(argIndex) {
            data class Choice(val op: String, val value: Double, val text: List<TextComponent>)
        }
    }
}
