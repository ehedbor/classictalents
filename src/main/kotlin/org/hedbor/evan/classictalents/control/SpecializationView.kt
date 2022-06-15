package org.hedbor.evan.classictalents.control

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.shape.Rectangle
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.model.Specialization
import org.hedbor.evan.classictalents.model.Talent
import org.hedbor.evan.classictalents.util.*

class SpecializationView(private val model: Specialization) : BorderPane() {
    companion object {
        private const val OVERLAP: Double = 2.0
    }

    @FXML private lateinit var iconView: ImageView
    @FXML private lateinit var specLabel: Label
    @FXML private lateinit var pointCounterLabel: Label
    @FXML private lateinit var talentGrid: GridPane
    @FXML private lateinit var arrowOverlay: Pane

    private val arrows = mutableMapOf<Talent, Node>()
    private val prereqChangedListeners = mutableMapOf<Talent, ChangeListener<Talent?>>()

    init {
        val loader = FXMLLoader(javaClass.getResource("$ASSETS_ROOT/view/SpecializationView.fxml"))
        loader.setRoot(this)
        loader.setController(this)
        loader.load<BorderPane>()
    }

    @FXML
    private fun initialize() {
        iconView.imageProperty().bind(model.iconProperty())
        specLabel.textProperty().bind(model.nameProperty())
        pointCounterLabel.textProperty().bind(
            model.allocatedPointsProperty().stringBinding { "($it)" })

        talentGrid.backgroundProperty().bind(
            model.backgroundProperty().objectBinding { img ->
                Background(BackgroundImage(
                    img,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    BackgroundSize(
                        100.0, 100.0,
                        true, true,
                        true, true)))
            })
        talentGrid.shape = Rectangle().apply {
            arcWidth = 20.0
            arcHeight = 20.0
            widthProperty().bind(talentGrid.widthProperty())
            heightProperty().bind(talentGrid.heightProperty())
        }

        model.talents.forEach { addTalent(it) }
        model.talents.addListener(ListChangeListener { c ->
            while (c.next()) {
                if (c.wasPermutated()) {
                    // do nothing; order is irrelevant
                } else if (c.wasUpdated()) {
                    // shouldn't need to be handled directly
                } else {
                    for (talent in c.removed) {
                        removeTalent(talent)
                    }
                    for (talent in c.addedSubList) {
                        addTalent(talent)
                    }
                }
            }
        })
    }

    @FXML
    private fun onResetButtonClicked(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            TODO("unallocate all points")
        }
    }

    private fun addTalent(talent: Talent) {
        val button = TalentButton(talent)
        talentGrid.add(button, talent.column, talent.row)
        GridPane.setMargin(button, Insets(15.0))

        // generate an arrow for talents with a prerequisite
        if (talent.prerequisite != null) {
            addArrow(talent)
        }

        // add/remove arrow if prerequisite prop changes
        val prereqListener = ChangeListener<Talent?> { _, old, new ->
            if (old != null) removeArrow(talent)
            if (new != null) addArrow(talent)
        }
        prereqChangedListeners[talent] = prereqListener
        talent.prerequisiteProperty().addListener(prereqListener)
    }

    private fun removeTalent(talent: Talent) {
        talentGrid.children.removeAll {
            GridPane.getRowIndex(it) == talent.row &&
                GridPane.getColumnIndex(it) == talent.column
        }

        removeArrow(talent)
        val listener = prereqChangedListeners.remove(talent)
        talent.prerequisiteProperty().removeListener(listener)
    }

    private fun addArrow(talent: Talent) {
        val talentButton = talentGrid.children.first {
            GridPane.getRowIndex(it) == talent.row &&
                GridPane.getColumnIndex(it) == talent.column
        }

        val prereq = talent.prerequisite!!
        val prereqButton = talentGrid.children.first { child ->
            prereq.row == GridPane.getRowIndex(child) &&
                prereq.column == GridPane.getColumnIndex(child)
        }

        // TODO: just completely regenerate arrow if row or col changes.
        val arrowHolder = Pane()
        if (talent.row != prereq.row) {
            arrowHolder.children += createVerticalArrow(talent, talentButton, prereq, prereqButton)
        }
        if (talent.column != prereq.column) {
            arrowHolder.children += createHorizontalArrow(talent, talentButton, prereq, prereqButton)
        }

        arrowOverlay.children.add(arrowHolder)
        arrows[talent] = arrowHolder
    }

    private fun removeArrow(talent: Talent) {
        talentGrid.children.removeIf {
            talent.row == GridPane.getRowIndex(it) &&
                talent.column == GridPane.getColumnIndex(it)
        }

        val arrow = arrows.remove(talent)
        arrowOverlay.children.removeIf { it == arrow }
    }

    private fun createVerticalArrow(
        talent: Talent, talentButton: Node,
        prereq: Talent, prereqButton: Node,
    ): Node {
        // Vertical arrows always go from top to bottom, so no need to worry about the reverse case.
        check(talent.row > prereq.row) {
            "Attempted to draw vertical arrow from bottom to top -- this is not allowed."
        }

        val talentBoundsProp = talentButton.boundsInParentProperty()
        val prereqBoundsProp = prereqButton.boundsInParentProperty()
        val horizImageProp = getHorizontalArrowImageProperty(talent, prereq)
        val vertImageProp = getVerticalArrowImageProperty(prereq)

        val arrow = ImageView()

        arrow.imageProperty().bind(vertImageProp)

        // Resize the arrow as needed
        arrow.viewportProperty().bind(arrow.imageProperty().objectBinding(
            horizImageProp, talentBoundsProp, prereqBoundsProp) { image ->
            // Calculate width and height
            val width = image.width

            val deltaY = talentBoundsProp.value.minY - prereqBoundsProp.value.maxY
            val height = deltaY + if (talent.column != prereq.column) { // horizontal
                (talentBoundsProp.value.height - horizImageProp.value.height) / 2.0 + OVERLAP
            } else {
                OVERLAP * 2.0
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
            talentBoundsProp, prereqBoundsProp) { image ->
            //isHoriz && !leftToRight
            //isHoriz => talent.col != prereq.col
            // leftToRight => talent.col > prereq.col
            if (talent.column < prereq.column) { // right to left
                talentBoundsProp.value.maxX - (prereqBoundsProp.value.width + image.width) / 2.0
            } else {
                talentBoundsProp.value.minX + (talentBoundsProp.value.width - image.width) / 2.0
            }
        })

        arrow.yProperty().bind(arrow.imageProperty().doubleBinding(
            talentBoundsProp, prereqBoundsProp) { image ->
            if (talent.column != prereq.column) { // horizontal
                prereqBoundsProp.value.maxY - (prereqBoundsProp.value.height - image.width) / 2.0
            } else {
                prereqBoundsProp.value.maxY - OVERLAP
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
            vertImageProp, prereqBoundsProp, talentBoundsProp) { image ->
            // determine width/height
            val height = image.height

            val deltaX = if (talent.column > prereq.column) { // left to right
                talentBoundsProp.value.minX - prereqBoundsProp.value.maxX
            } else {
                prereqBoundsProp.value.minX - talentBoundsProp.value.maxX
            }

            val width = deltaX + if (talent.row != prereq.row) { // vertical
                (talentBoundsProp.value.width + vertImageProp.value.width) / 2.0 + OVERLAP
            } else {
                OVERLAP * 2.0
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
            vertImageProp, talentBoundsProp, prereqBoundsProp) {
            if (talent.row != prereq.row) { // vertical
                if (talent.column > prereq.column) { // left to right
                    prereqBoundsProp.value.maxX - OVERLAP
                } else {
                    talentBoundsProp.value.maxX - (prereqBoundsProp.value.width + vertImageProp.value.width) / 2.0
                }
            } else {
                if (talent.column > prereq.column) { // left to right
                    prereqBoundsProp.value.maxX - OVERLAP
                } else {
                    talentBoundsProp.value.maxX - OVERLAP
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
            val url = if (prereq.rank < prereq.maxRank) {
                "$ASSETS_ROOT/images/arrows/down.png"
            } else {
                "$ASSETS_ROOT/images/arrows/down2.png"
            }
            Image(javaClass.getResourceAsStream(url))
        }
    }

    private fun getHorizontalArrowImageProperty(
        talent: Talent, prereq: Talent
    ): ObservableValue<Image> {
        return objectBinding(prereq.rankProperty(), prereq.maxRankProperty()) {
            val horizComponent = if (talent.column > prereq.column) "right" else "left"
            val vertComponent = if (talent.row > prereq.row) "down" else ""
            val rankComponent = if (prereq.rank < prereq.maxRank) "" else "2"

            val imageName = "$horizComponent$vertComponent$rankComponent.png"
            Image(javaClass.getResourceAsStream("$ASSETS_ROOT/images/arrows/$imageName"))
        }
    }
}