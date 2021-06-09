package org.hedbor.evan.talenttreegenerator.view

import tornadofx.*


class MenuBarView : View() {
    override val root = menubar {
        menu("File") {
            item("New", "Shortcut+N") {
            }
            item("Open", "Shortcut+O") {
                isMnemonicParsing = true
                action {
                }
            }
            item("_Save", "Shortcut+S") {
                isMnemonicParsing = true
                action {
                }
            }
            item("Save As", "Shortcut+Shift+S")
            separator()
            item( "Exit") {
                action {
                }
            }
        }
        menu("Edit")
        menu("View")
        menu("Help")
    }
}