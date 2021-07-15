package org.hedbor.evan.classictalents.talentgen.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.scene.control.ButtonBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.util.StringConverter
import javafx.util.converter.NumberStringConverter
import org.hedbor.evan.classictalents.common.model.CooldownUnit
import org.hedbor.evan.classictalents.common.model.ResourceType
import org.hedbor.evan.classictalents.common.model.Specialization
import org.hedbor.evan.classictalents.common.model.Talent
import org.hedbor.evan.classictalents.talentgen.INITIAL_ICON_DIRECTORY
import org.hedbor.evan.classictalents.talentgen.chooseIconFromResources
import org.hedbor.evan.classictalents.talentgen.model.SpecializationModel
import org.hedbor.evan.classictalents.talentgen.model.TalentModel
import org.hedbor.evan.classictalents.talentgen.model.WowClassModel
import tornadofx.*


class TalentEditor : Fragment() {
    companion object {
        private const val SMALL_TEXT_FIELD_PREF_WIDTH = 40.0
        private const val COMBO_BOX_PREF_WIDTH = 70.0
        private const val ROW_COL_LABEL_MIN_WIDTH = 23.0
        private const val ROW_COL_FIELD_PREF_WIDTH = 50.0
    }

    private val wowClassModel: WowClassModel by inject()
    private val specModel: SpecializationModel by inject()
    private val model: TalentModel by inject()

    private val isSpellProperty = SimpleBooleanProperty()

    init {
        title = messages["editor.title.talent"]
    }

    override val root = borderpane {
        center {
            squeezebox {
                fold(messages["editor.fieldset.general"]) {
                    addGeneralInfo()
                    isExpanded = true
                    isAnimated = false
                    heightProperty().addListener { _, _, _ ->
                        currentStage?.sizeToScene()
                    }
                }
                fold(messages["editor.fieldset.spell"]) {
                    addSpellInfo()
                    isAnimated = false
                    heightProperty().addListener { _, _, _ ->
                        currentStage?.sizeToScene()
                    }
                }
            }
        }
        bottom {
            val buttonBar = addButtonBar()
            BorderPane.setMargin(buttonBar, Insets(5.0))
        }
    }

    private fun Region.addGeneralInfo() = form {
        fieldset(messages["editor.fieldset.general"]) {
            field(messages["editor.field.translation_key"]) {
                label(wowClassModel.translationKey.stringBinding(specModel.translationKey) {
                    "$it.${specModel.translationKey.value}."
                })
                textfield(model.translationKey) {
                    mustBePresent()
                    mustBeValidKey()
                }
            }
            field(messages["editor.field.icon"]) {
                textfield(model.icon) {
                    mustBePresent()
                    isEditable = false
                }
                button("...") {
                    action {
                        val icon = chooseIconFromResources(messages["action.file.choose.icon"], INITIAL_ICON_DIRECTORY)
                        if (icon != null)
                            model.icon.value = icon
                    }
                }
            }
            field(messages["editor.field.max_rank"]) {
                combobox(model.maxRank, (Talent.MINIMUM_RANK..Talent.MAXIMUM_PERMISSIBLE_RANK).toList()) {
                    // prefWidth = COMBO_BOX_PREF_WIDTH
                    if (selectionModel.selectedItem !in items) {
                        selectionModel.selectLast()
                    }
                }
            }
            field(messages["editor.field.prerequisite"]) {
                label(messages["editor.field.row"]) {
                    minWidth = ROW_COL_LABEL_MIN_WIDTH
                }
                textfield(model.prerequisite.select { it.rowProperty }, NumberStringConverter()) {
                    prefWidth = ROW_COL_FIELD_PREF_WIDTH
                    model.validationContext.mustBeInRange(this, 0 until wowClassModel.era.value.talentRowCount)
                }
                label(messages["editor.field.column"]) {
                    minWidth = ROW_COL_LABEL_MIN_WIDTH
                }
                textfield(model.prerequisite.select { it.columnProperty }, NumberStringConverter()) {
                    prefWidth = ROW_COL_FIELD_PREF_WIDTH
                    model.validationContext.mustBeInRange(this, 0 until Specialization.TALENT_COLUMN_COUNT)
                }
            }
        }
    }

    private fun Region.addSpellInfo() = form {
        fieldset(messages["editor.fieldset.spell"]) {
            val textFieldWidth = widthProperty().divide(2.75)
            val comboBoxWidth = widthProperty().divide(3)


            field(messages["editor.field.is_spell"]) {
                checkbox(property = isSpellProperty)
            }
            field(messages["editor.field.resource"]) {
                textfield(model.spell.select { it.resourceCostProperty }, NumberStringConverter()) {
                    // prefWidth = SMALL_TEXT_FIELD_PREF_WIDTH
                    prefWidthProperty().bind(textFieldWidth)
                    maxWidthProperty().bind(textFieldWidth)
                    enableWhen(isSpellProperty)
                    tooltip(messages["editor.field.resource.tooltip"])
                    model.validationContext.mustBeNonNegative<Int>(this)
                }
                combobox(model.spell.select { it.resourceTypeProperty }, ResourceType.values().toList()) {
                    // prefWidth = COMBO_BOX_PREF_WIDTH
                    prefWidthProperty().bind(comboBoxWidth)
                    maxWidthProperty().bind(comboBoxWidth)
                    enableWhen(isSpellProperty)
                    converter = object : StringConverter<ResourceType>() {
                        override fun toString(`object`: ResourceType?): String {
                            return if (`object` == null) "" else messages[`object`.translationKey]
                        }

                        override fun fromString(string: String?): ResourceType {
                            throw IllegalStateException("unreachable")
                        }
                    }
                    if (selectionModel.selectedItem !in items) {
                        selectionModel.selectFirst()
                    }
                }
            }
            field(messages["editor.field.cast_time"]) {
                textfield(model.spell.select { it.castTimeProperty }, NumberStringConverter()) {
                    // prefWidth = SMALL_TEXT_FIELD_PREF_WIDTH
                    prefWidthProperty().bind(textFieldWidth)
                    maxWidthProperty().bind(textFieldWidth)
                    enableWhen(isSpellProperty)
                    tooltip(messages["editor.field.cast_time.tooltip"])
                    model.validationContext.mustBeNonNegative<Double>(this)
                }
                label(messages["unit.time.seconds"]) {
                    // prefWidth = COMBO_BOX_PREF_WIDTH
                    prefWidthProperty().bind(comboBoxWidth)
                    maxWidthProperty().bind(comboBoxWidth)
                    enableWhen(isSpellProperty)
                }
            }
            field(messages["editor.field.cooldown"]) {
                textfield(model.spell.select { it.cooldownProperty }, NumberStringConverter()) {
                    // prefWidth = SMALL_TEXT_FIELD_PREF_WIDTH
                    prefWidthProperty().bind(textFieldWidth)
                    maxWidthProperty().bind(textFieldWidth)
                    enableWhen(isSpellProperty)
                    tooltip(messages["editor.field.cooldown.tooltip"])
                    model.validationContext.mustBeNonNegative<Double>(this)
                }
                combobox(model.spell.select { it.cooldownUnitProperty }, CooldownUnit.values().toList()) {
                    // prefWidth = COMBO_BOX_PREF_WIDTH
                    prefWidthProperty().bind(comboBoxWidth)
                    maxWidthProperty().bind(comboBoxWidth)
                    enableWhen(isSpellProperty)
                    converter = object : StringConverter<CooldownUnit>() {
                        override fun toString(`object`: CooldownUnit?): String {
                            return if (`object` == null) "" else messages[`object`.translationKey]
                        }

                        override fun fromString(string: String?): CooldownUnit {
                            throw IllegalStateException("unreachable")
                        }
                    }
                    if (selectionModel.selectedItem !in items) {
                        selectionModel.selectFirst()
                    }
                }
            }
            field(messages["editor.field.range"]) {
                textfield(model.spell.select { it.rangeProperty }, NumberStringConverter()) {
                    // prefWidth = SMALL_TEXT_FIELD_PREF_WIDTH
                    prefWidthProperty().bind(textFieldWidth)
                    maxWidthProperty().bind(textFieldWidth)
                    enableWhen(isSpellProperty)
                    tooltip(messages["editor.field.range.tooltip"])
                    model.validationContext.mustBeNonNegative<Double>(this)
                }
                label(messages["unit.distance.yards"]) {
                    // prefWidth = COMBO_BOX_PREF_WIDTH
                    prefWidthProperty().bind(comboBoxWidth)
                    maxWidthProperty().bind(comboBoxWidth)
                    enableWhen(isSpellProperty)
                }
            }
        }
    }

    private fun Region.addButtonBar() = buttonbar {
        button(messages["action.editor.apply"], ButtonBar.ButtonData.APPLY) {
            enableWhen(model.dirty.and(model.valid))
            action { saveEditor() }
        }
        button(messages["action.editor.ok"], ButtonBar.ButtonData.OK_DONE) {
            enableWhen(model.valid)
            action { saveAndCloseEditor() }
        }
        button(messages["action.editor.cancel"], ButtonBar.ButtonData.CANCEL_CLOSE) {
            action { closeEditor() }
        }
    }

    private fun saveEditor() {
        model.commit {
            specModel.talents.invalidate()
        }
    }

    private fun saveAndCloseEditor() {
        model.commit {
            specModel.talents.invalidate()
            close()
        }
    }

    private fun closeEditor() {
        fun rollbackAndClose() {
            model.rollback()
            close()
        }

        if (!model.dirty.value) {
            rollbackAndClose()
        } else {
            confirm(
                messages["confirm.discard"],
                messages.format("confirm.discard.content", messages["this.talent"]),
                actionFn = ::rollbackAndClose)
        }
    }
}