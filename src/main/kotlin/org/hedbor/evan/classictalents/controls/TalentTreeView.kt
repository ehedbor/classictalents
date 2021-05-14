package org.hedbor.evan.classictalents.controls

import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.layout.GridPane
import org.hedbor.evan.classictalents.styles.TalentTreeStyles
import org.hedbor.evan.classictalents.talents.TalentTree
import tornadofx.addClass
import tornadofx.gridpaneConstraints
import tornadofx.opcr
import tornadofx.style
import java.net.URI
import java.util.*


class TalentTreeView(private val talentTree: TalentTree, messages: ResourceBundle) : GridPane() {
    init {
        style {
            backgroundImage += URI(talentTree.backgroundImage)
        }
        addClass(TalentTreeStyles.talentTreeBackground)

        for (talent in talentTree.talents) {
            talentbutton(talent, messages) {
                gridpaneConstraints {
                    columnRowIndex(talent.location.second, talent.location.first)
                    padding = Insets(10.0)
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
}

fun EventTarget.talenttreeview(talentTree: TalentTree, messages: ResourceBundle, op: TalentTreeView.() -> Unit = {}) =
    opcr(this, TalentTreeView(talentTree, messages).apply(op))
