package org.hedbor.evan.talenttreegenerator.view

import javafx.scene.control.TabPane
import org.hedbor.evan.talenttreegenerator.controller.TalentTreeController
import tornadofx.*

class MainView : View("Talent Tree Generator") {
    val controller = find<TalentTreeController>()

    override val root = borderpane {
        //top<MenuBarView>()
        center {
            tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                tab<WowClassEditor>(Scope(controller.model))
                tab<SpecializationEditor>(getScopeForSpec(1))
                tab<SpecializationEditor>(getScopeForSpec(2))
                tab<SpecializationEditor>(getScopeForSpec(3))
            }
        }
    }

    private fun getScopeForSpec(index: Int): Scope {
        val spec = when (index) {
            1 -> controller.model.spec1
            2 -> controller.model.spec2
            3 -> controller.model.spec3
            else -> throw IllegalArgumentException("Spec index must be in the range 1-3")
        }

        if (spec.displayName.value.isNullOrEmpty()) {
            spec.displayName.value = "Specialization $index"
        }

        return Scope(controller.model, spec)
    }
}