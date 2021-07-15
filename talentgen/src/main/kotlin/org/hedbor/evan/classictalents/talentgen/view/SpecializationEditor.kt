package org.hedbor.evan.classictalents.talentgen.view

import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.ButtonBar
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.text.FontWeight
import org.hedbor.evan.classictalents.common.model.Location
import org.hedbor.evan.classictalents.common.model.Specialization
import org.hedbor.evan.classictalents.common.model.Talent
import org.hedbor.evan.classictalents.talentgen.APP_RESOURCES_DIRECTORY
import org.hedbor.evan.classictalents.talentgen.INITIAL_BACKGROUND_DIRECTORY
import org.hedbor.evan.classictalents.talentgen.chooseIconFromResources
import org.hedbor.evan.classictalents.talentgen.model.SpecializationModel
import org.hedbor.evan.classictalents.talentgen.model.TalentModel
import org.hedbor.evan.classictalents.talentgen.model.WowClassModel
import tornadofx.*
import java.net.MalformedURLException


class SpecializationEditor : Fragment() {
    private val wowClassModel: WowClassModel by inject()
    private val model: SpecializationModel by inject()

    init {
        title = messages["editor.title.spec"]
    }

    override val root = borderpane {
        center { addGeneralInfo() }
        bottom { addButtonBar() }
        children.forEach {
            BorderPane.setMargin(it, Insets(0.0, 5.0, 5.0, 5.0))
        }
    }

    private fun Region.addGeneralInfo() = form {
        fieldset(messages["editor.fieldset.spec"]) {
            field(messages["editor.field.translation_key"]) {
                label(wowClassModel.translationKey.stringBinding { "$it." })
                textfield(model.translationKey) {
                    mustBePresent()
                    mustBeValidKey()
                }
            }
            field(messages["editor.field.background"]) {
                textfield(model.backgroundImage) {
                    isEditable = false
                    mustBePresent()
                }
                button("...") {
                    action {
                        val image = chooseIconFromResources(messages["action.file.choose.background"], INITIAL_BACKGROUND_DIRECTORY)
                        if (image != null) model.backgroundImage.value = image
                    }
                }
            }
        }
        fieldset(messages["editor.field.talents"]) {
            gridpane {
                for (row in 0 until wowClassModel.era.value.talentRowCount) {
                    for (col in 0 until Specialization.TALENT_COLUMN_COUNT) {
                        talentbutton(Location(row, col))
                    }
                }
            }
        }
    }

    private fun Region.addButtonBar() = buttonbar {
        button(messages["action.editor.apply"], ButtonBar.ButtonData.APPLY) {
            enableWhen(model.valid.and(model.dirty))
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

    private fun EventTarget.talentbutton(location: Location): StackPane {
        return stackpane talentButtonRoot@ {
            val talent = getOrCreateTalentAt(location)
            gridpaneConstraints {
                columnRowIndex(location.column, location.row)
                margin = Insets(2.0)
            }
            button {
                prefWidth = 70.0
                prefHeight = 70.0
                maxWidthProperty().bind(prefWidthProperty())
                maxHeightProperty().bind(prefHeightProperty())
                graphicProperty().bind(talent.iconProperty.objectBinding { getTalentGraphic(it) })
                action { editTalent(talent) }
            }
            anchorpane {
                // prevent the anchor pane from eating click events to the editButton
                // unless the delete button is being clicked
                isPickOnBounds = false
                button("X") {
                    anchorpaneConstraints {
                        topAnchor = 1.0
                        rightAnchor = 1.0
                    }
                    style {
                        fontWeight = FontWeight.BOLD
                    }
                    prefWidth = 25.0
                    prefHeight = 25.0
                    padding = Insets(1.0)
                    action {
                        resetTalent(location, this@talentButtonRoot)
                    }
                }
            }
        }
    }

    private fun getTalentGraphic(icon: String?): Node? {
        if (icon.isNullOrBlank()) return null
        return try {
            val formattedPath = APP_RESOURCES_DIRECTORY.resolve(icon).toURI().toURL().toExternalForm()
            ImageView(formattedPath).also {
                it.fitWidth = 50.0
                it.isPreserveRatio = true
                it.isSmooth = true
            }
        } catch (e: Exception) {
            when (e) {
                is IllegalArgumentException,
                is MalformedURLException,
                is SecurityException -> null
                else -> throw e
            }
        }
    }

    private fun getOrCreateTalentAt(location: Location): Talent {
        var talent = model.talents.find { it.location == location  }
        if (talent == null) {
            talent = Talent { this.location = location }
            model.talents += talent
        }
        return talent
    }

    private fun editTalent(talent: Talent) {
        val talentModel = TalentModel(talent)
        val scope = Scope(wowClassModel, model, talentModel)
        find<TalentEditor>(scope).openModal()
    }

    /**
     * Deletes the original talent and [talentButtonRoot] and creates a new talent button.
     */
    private fun resetTalent(location: Location, talentButtonRoot: Region) {
        confirm(
            messages.format("confirm.reset", messages["this.talent"]),
            messages.format("confirm.reset.content", messages["this.talent"])
        ) {
            val parent = talentButtonRoot.parent
            talentButtonRoot.removeFromParent()
            model.talents.removeIf { it.location == location }

            parent.talentbutton(location)
        }
    }


    private fun saveEditor() {
        model.commit {
            wowClassModel.specializations.invalidate()
        }
    }

    private fun saveAndCloseEditor() {
        saveEditor()
        close()
    }

    private fun closeEditor() {
        fun rollbackAndClose() {
            model.rollback()
            this.close()
        }

        if (!model.dirty.value) {
            rollbackAndClose()
        } else {
            confirm(
                messages["confirm.discard"],
                messages.format("confirm.discard.content", messages["this.spec"]),
                actionFn = ::rollbackAndClose)
        }
    }
}