package org.hedbor.evan.talenttreegenerator

import javafx.scene.control.ButtonBar
import javafx.stage.FileChooser
import javafx.util.converter.NumberStringConverter
import org.hedbor.evan.talenttreegenerator.model.TalentModel
import tornadofx.*
import java.io.File


class TalentEditor : Fragment("Talent Editor") {
    companion object {
        private val IMAGE_FILES_FILTER = FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.gif", "*.jpg", "*.png")
        private val INITIAL_DIRECTORY = File("./src/main/resources/images/Classic").absoluteFile
        private val RESOURCES_DIRECTORY = File("./src/main/resources").absoluteFile
    }

    val model : TalentModel by inject()

    override val root = form {
        fieldset("Edit Talent") {
            field("Display Name *") {
                textfield(model.displayName).required()
            }
            field("Translation Key *") {
                val useCustomTranslationKeyCheckBox = checkbox {
                    action {
                        if (isSelected) {
                            unbindTranslationKey()
                        } else {
                            bindTranslationKey()
                        }
                    }
                }
                bindTranslationKey()

                textfield(model.translationKey) {
                    required()
                    enableWhen(useCustomTranslationKeyCheckBox.selectedProperty())
                }
            }
            field("Icon *") {
                textfield(model.icon).required()
                button("...") {
                    action {
                        val files = chooseFile("Choose an Icon", arrayOf(IMAGE_FILES_FILTER), INITIAL_DIRECTORY)
                        if (files.isEmpty()) return@action
                        val file = files[0]

                        val inResourcesDir = file.canonicalPath.contains(RESOURCES_DIRECTORY.canonicalPath + File.separator)
                        if (inResourcesDir) {
                            var path = file.relativeTo(RESOURCES_DIRECTORY).path
                            path = '/' + path.replace(File.separatorChar, '/')
                            model.icon.value = path
                        } 
                    }
                }
            }
            field("Max Rank *") {
                combobox(model.maxRank, listOf(1, 2, 3, 4, 5)).required()
            }
            field("Location *") {
                label("Row")
                textfield(model.location.row, NumberStringConverter()).required()
                label("Col")
                textfield(model.location.column, NumberStringConverter()).required()
            }
            field("Prerequisite") {
                checkbox(property = model.hasPrerequisite)
                label("Row")
                textfield(model.prerequisite.row, NumberStringConverter()) {
                    enableWhen(model.hasPrerequisite)
                }
                label("Col")
                textfield(model.prerequisite.column, NumberStringConverter()) {
                    enableWhen(model.hasPrerequisite)
                }
            }
            field("Description *") {
                textarea(model.description).required()
            }
        }
        fieldset("Spell Info") {
            field("Is spell") {
                checkbox(property = model.isSpell)
            }
            field("Resource") {
                checkbox(property = model.spellInfo.hasResource)
                textfield(model.spellInfo.resourceCost, NumberStringConverter()) {
                    enableWhen(model.spellInfo.hasResource)
                }
                combobox(model.spellInfo.resourceType, listOf("mana", "% of base mana", "energy", "rage")) {
                    enableWhen(model.spellInfo.hasResource)
                }
                enableWhen(model.isSpell)
            }
            field("Cast Time") {
                checkbox(property = model.spellInfo.isInstantCast)
                textfield(model.spellInfo.castTime, NumberStringConverter()) {
                    enableWhen(model.spellInfo.isInstantCast)
                }
                label("sec")     
                enableWhen(model.isSpell)
            }
            field("Cooldown") {
                checkbox(property = model.spellInfo.hasCooldown)
                textfield(model.spellInfo.cooldown, NumberStringConverter()) {
                    enableWhen(model.spellInfo.hasCooldown)
                }
                combobox(model.spellInfo.cooldownUnit, listOf("sec", "min", "hr")) {
                    enableWhen(model.spellInfo.hasCooldown)
                }
                enableWhen(model.isSpell)
            }
            field("Range") {
                checkbox(property = model.spellInfo.isNotMeleeRange)
                textfield(model.spellInfo.range, NumberStringConverter()) {
                    enableWhen(model.spellInfo.isNotMeleeRange)
                }
                label("yd")
                enableWhen(model.isSpell)
            }
        }
        buttonbar {
            button("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)
            button("OK", ButtonBar.ButtonData.OK_DONE)
        }
    }

    private fun bindTranslationKey() {
        model.translationKey.bind(model.displayName.stringBinding {
            "warlock.spec1." + (it?.lowercase()?.replace(' ', '_') ?: "")
        })
    }

    private fun unbindTranslationKey() {
        model.translationKey.unbind()
    }
}