package org.hedbor.evan.classictalents.controls

import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.layout.GridPane
import org.hedbor.evan.classictalents.styles.TalentTreeStyles
import org.hedbor.evan.classictalents.talents.Talent
import org.hedbor.evan.classictalents.talents.TalentTree
import tornadofx.*
import java.net.URI
import java.util.*


class TalentTreeView(val talentTree: TalentTree, val messages: ResourceBundle) : GridPane() {
    init {
        style {
            backgroundImage += URI(talentTree.backgroundImage)
        }
        addClass(TalentTreeStyles.talentTreeBackground)

        for (talent in talentTree.talents) {
            talentbutton(talent, messages) {
                gridpaneConstraints {
                    columnRowIndex(talent.location.second, talent.location.first)
                    padding = Insets(5.0)
                }
            }
        }
    }

    override fun computeMinWidth(height: Double): Double {
        return computePrefWidth(height)
    }

    override fun computeMinHeight(width: Double): Double {
        return computePrefHeight(width)
    }

    override fun computeMaxWidth(height: Double): Double {
        return computePrefWidth(height)
    }

    override fun computeMaxHeight(width: Double): Double {
        return computePrefHeight(width)
    }


    override fun getUserAgentStylesheet(): String = TalentTreeStyles().base64URL.toExternalForm()
}

fun EventTarget.talenttreeview(talentTree: TalentTree, messages: ResourceBundle, op: TalentTreeView.() -> Unit = {}) =
    opcr(this, TalentTreeView(talentTree, messages).apply(op))
