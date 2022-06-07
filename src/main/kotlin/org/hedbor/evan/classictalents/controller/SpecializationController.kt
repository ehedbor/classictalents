package org.hedbor.evan.classictalents.controller

import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.shape.Rectangle
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.model.Specialization
import org.hedbor.evan.classictalents.model.Talent
import org.hedbor.evan.classictalents.util.doubleBinding
import org.hedbor.evan.classictalents.util.objectBinding

class SpecializationController {
    companion object {
        /** adjusted button insets */
        private const val ADJUSTED_INSETS: Double = 5.0
    }

    lateinit var specialization: Specialization

    @FXML private lateinit var iconView: ImageView
    @FXML private lateinit var specLabel: Label
    @FXML private lateinit var pointCounterLabel: Label
    @FXML private lateinit var talentGrid: GridPane
    @FXML private lateinit var arrowOverlay: Pane

    @FXML
    private fun initialize() {
        specLabel.text = "Affliction"
        pointCounterLabel.text = "31 points"

        initializeTalentGrid()
        initializeTalentArrowOverlay()
    }

    @FXML
    private fun onResetButtonClicked(event: MouseEvent) {
        TODO()
    }

    private fun initializeTalentGrid() {
        talentGrid.backgroundProperty().bind(specialization.backgroundProperty().objectBinding { path ->
            Background(BackgroundImage(
                Image("$ASSETS_ROOT/$path"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize(
                    100.0, 100.0,
                    true, true,
                    false, false)
            ))
        })

        talentGrid.shape = Rectangle().apply {
            arcWidth = 20.0
            arcHeight = 20.0
            widthProperty().bind(talentGrid.widthProperty())
            heightProperty().bind(talentGrid.heightProperty())
        }

        val loader = FXMLLoader(javaClass.getResource("$ASSETS_ROOT/views/TalentButtonView.fxml"))
        for (talent in specialization.talents) {
            val controller = TalentButtonController().also { it.talent = talent }
            loader.setController(controller)

            val button = loader.load<Region>()
            talentGrid.add(button, talent.column, talent.row)
        }
    }

    private fun initializeTalentArrowOverlay() {
        for (talentButton in talentGrid.children) {
            val row = GridPane.getRowIndex(talentButton)
            val column = GridPane.getColumnIndex(talentButton)
            val talent = specialization.talents.first { it.row == row && it.column == column }

            // only generate arrows for talents with a requirement
            val prerequisite = talent.prerequisite ?: continue

            val prerequisiteButton = talentGrid.children.first { child ->
                prerequisite.row == GridPane.getRowIndex(child) &&
                    prerequisite.column == GridPane.getColumnIndex(child)
            }

            val arrow = if (talent.row != prerequisite.row) {
                createVerticalArrow(talent, talentButton, prerequisite, prerequisiteButton)
            } else if (talent.column != prerequisite.column) {
                createHorizontalArrow(talent, talentButton, prerequisite, prerequisiteButton)
            } else {
                throw IllegalStateException("Talent '${talent.name}' (in spec '${specialization.name}')" +
                    " has itself as a prerequisite")
            }

            arrowOverlay.children.add(arrow)
        }
    }


    private fun createVerticalArrow(
        talent: Talent, talentButton: Node,
        prereq: Talent, prereqButton: Node,
    ): Node {
        // Vertical arrows always go from top to bottom, so no need to worry about the reverse case.
        check(talent.row > prereq.row) { "Attempted to draw vertical arrow from bottom to top -- this is not allowed." }

        val talentBoundsProp = talentButton.boundsInParentProperty()
        val prereqBoundsProp = prereqButton.boundsInParentProperty()
        val horizImageProp = getHorizontalArrowImageProperty(talent, prereq)
        val vertImageProp = getVerticalArrowImageProperty(prereq)

        val arrow = ImageView()

        arrow.imageProperty().bind(vertImageProp)

        // Resize the arrow as needed
        arrow.viewportProperty().bind(arrow.imageProperty().objectBinding(
            talentBoundsProp, talent.columnProperty(),
            prereqBoundsProp, prereq.columnProperty(),
            horizImageProp,
        ) { image ->
            // Calculate width and height
            val width = image.width

            val deltaY = talentBoundsProp.value.minY - prereqBoundsProp.value.maxY
            val height = deltaY + if (talent.column != prereq.column) { // horizontal
                (talentBoundsProp.value.height - horizImageProp.value.height) / 2.0 + ADJUSTED_INSETS
            } else {
                ADJUSTED_INSETS * 2.0
            }

            // Calculate minimum x/y
            val minX = 0.0
            val minY = image.height - height

            if (width >= 0 && height >= 0) {
                Rectangle2D(minX, minY, width, height)
            } else {
                null
            }
        })

        // Move the arrow as needed
        arrow.xProperty().bind(arrow.imageProperty().doubleBinding(
            talentBoundsProp, talent.columnProperty(),
            prereqBoundsProp, prereq.columnProperty(),
        ) { image ->
            if (talent.column < prereq.column) { // right to left
                talentBoundsProp.value.maxX - (prereqBoundsProp.value.width + image.width) / 2.0
            } else {
                prereqBoundsProp.value.maxX - (talentBoundsProp.value.width - image.width) / 2.0
            }
        })

        arrow.yProperty().bind(arrow.imageProperty().doubleBinding(
            talentBoundsProp, talent.columnProperty(),
            prereqBoundsProp, prereq.columnProperty(),
        ) { image ->
            if (talent.column != prereq.column) { // horizontal
                prereqBoundsProp.value.maxY - (prereqBoundsProp.value.height - image.width) / 2.0
            } else {
                prereqBoundsProp.value.maxY - ADJUSTED_INSETS
            }
        })

        return arrow
    }

    private fun createHorizontalArrow(
        talent: Talent, talentButton: Node,
        prereq: Talent, prereqButton: Node,
    ): Node {
        val talentBoundsProp = talentButton.boundsInParentProperty()
        val prereqBoundsProp = prereqButton.boundsInParentProperty()
        val horizImageProp = getHorizontalArrowImageProperty(talent, prereq)
        val vertImageProp = getVerticalArrowImageProperty(prereq)

        val arrow = ImageView()

        arrow.imageProperty().bind(horizImageProp)

        // Resize the arrow as needed
        arrow.viewportProperty().bind(arrow.imageProperty().objectBinding(
            vertImageProp,
            prereqBoundsProp, prereq.rowProperty(), prereq.columnProperty(),
            talentBoundsProp, talent.rowProperty(), talent.columnProperty(),
        ) { image ->
            // determine width/height
            val height = image.height

            val deltaX = if (talent.column > prereq.column) { // left to right
                talentBoundsProp.value.minX - prereqBoundsProp.value.maxX
            } else {
                prereqBoundsProp.value.minX - talentBoundsProp.value.maxX
            }

            val width = deltaX + if (talent.row != prereq.row) { // vertical
                (talentBoundsProp.value.minX + vertImageProp.value.width) / 2.0 + ADJUSTED_INSETS
            } else {
                ADJUSTED_INSETS * 2.0
            }

            // determine min x/y
            val minY = 0.0
            val minX = if (talent.column > prereq.column) image.width - width else 0.0

            if (width >= 0 && height >= 0) {
                Rectangle2D(minX, minY, width, height)
            } else {
                null
            }
        })

        // move the arrow as needed
        arrow.xProperty().bind(doubleBinding(
            vertImageProp,
            talentBoundsProp, talent.rowProperty(), talent.columnProperty(),
            prereqBoundsProp, prereq.rowProperty(), prereq.columnProperty(),
        ) {
            if (talent.row != prereq.row) { // vertical
                if (talent.column > prereq.column) { // left to right
                    prereqBoundsProp.value.maxX - ADJUSTED_INSETS
                } else {
                    talentBoundsProp.value.maxX - (prereqBoundsProp.value.width + vertImageProp.value.width) / 2.0
                }
            } else {
                if (talent.column > prereq.column) { // left to right
                    prereqBoundsProp.value.maxX - ADJUSTED_INSETS
                } else {
                    talentBoundsProp.value.maxX - ADJUSTED_INSETS
                }
            }
        })

        arrow.yProperty().bind(arrow.imageProperty().doubleBinding(
            talentBoundsProp, prereqBoundsProp) { image ->
            (prereqBoundsProp.value.minY + prereqBoundsProp.value.maxY - image.height) / 2.0
        })

        return arrow
    }

    private fun getVerticalArrowImageProperty(prereq: Talent): ObservableValue<Image> {
        return objectBinding(prereq.rankProperty(), prereq.maxRankProperty()) {
            if (prereq.rank < prereq.maxRank) {
                Image("$ASSETS_ROOT/images/arrows/down.png")
            } else {
                Image("$ASSETS_ROOT/images/arrows/down2.png")
            }
        }
    }


    private fun getHorizontalArrowImageProperty(
        talent: Talent, prereq: Talent
    ): ObservableValue<Image> {
        return objectBinding(
            prereq.rankProperty(), prereq.maxRankProperty(),
            talent.rowProperty(), talent.columnProperty(),
            prereq.rowProperty(), prereq.columnProperty()
        ) {
            val horizComponent = if (talent.column > prereq.column) "right" else "left"
            val vertComponent = if (talent.row > prereq.row) "down" else ""
            val rankComponent = if (prereq.rank < prereq.maxRank) "" else "2"

            val imageName = "$horizComponent$vertComponent$rankComponent.png"
            Image("$ASSETS_ROOT/images/arrows/$imageName")
        }
    }
}