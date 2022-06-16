package org.hedbor.evan.classictalents.control

import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.hedbor.evan.classictalents.ASSETS_ROOT
import org.hedbor.evan.classictalents.model.CooldownUnit
import org.hedbor.evan.classictalents.model.ResourceType
import org.hedbor.evan.classictalents.model.Talent
import org.hedbor.evan.classictalents.util.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class TalentButton(private val model: Talent) : StackPane() {
    @FXML private lateinit var button: Button

    @FXML private lateinit var iconView: ImageView
    @FXML private lateinit var highlightView: ImageView
    @FXML private lateinit var activeBorderRegion: Region
    @FXML private lateinit var rankCounterLabel: Label

    @FXML private lateinit var tooltipNameLabel: Label
    @FXML private lateinit var tooltipRankLabel: Label

    @FXML private lateinit var tooltipSpellPane: VBox
    @FXML private lateinit var tooltipCostLabel: Label
    @FXML private lateinit var tooltipRangeLabel: Label
    @FXML private lateinit var tooltipCastTimeLabel: Label
    @FXML private lateinit var tooltipCooldownLabel: Label

    @FXML private lateinit var tooltipDescLabel: Label
    @FXML private lateinit var tooltipNextRankPane: VBox
    @FXML private lateinit var tooltipNextRankDescLabel: Label

    @FXML private lateinit var tooltipSpecRequiredLabel: Label
    @FXML private lateinit var tooltipPrereqRequiredLabel: Label
    @FXML private lateinit var tooltipClickToLearnLabel: Label
    @FXML private lateinit var tooltipClickToUnlearnLabel: Label

    private val rankListener = ChangeListener<Number?> { _, _, _ -> updateActiveBorder() }

    init {
        val loader = FXMLLoader(javaClass.getResource("$ASSETS_ROOT/view/TalentButtonView.fxml"))
        loader.setRoot(this)
        loader.setController(this)
        loader.load<StackPane>()
    }

    @Suppress("unused")
    @FXML
    private fun initialize() {
        highlightView.visibleProperty().bind(button.hoverProperty())
        iconView.imageProperty().bind(model.iconProperty())

        rankCounterLabel.textProperty().bind(model.rankProperty().asString())

        model.rankProperty().addListener(rankListener)
        model.maxRankProperty().addListener(rankListener)

        initTooltip()
    }

    private fun initTooltip() {
        initHeader()
        initSpell()
        initDesc()
        initFooter()
    }

    private fun initHeader() {
        tooltipNameLabel.textProperty().bind(model.nameProperty())
        tooltipRankLabel.textProperty().bind(
            model.rankProperty().stringBinding(model.maxRankProperty()) { rank ->
                "Rank $rank/${model.maxRank}"
            })
    }

    private fun initSpell() {
        tooltipSpellPane.visibleProperty().bind(model.spellProperty().isNotNull)
        tooltipSpellPane.managedProperty().bind(model.spellProperty().isNotNull)

        val resourceCost = Bindings.select<Int?>(model.spellProperty(), "cost")
        val resource = Bindings.select<ResourceType?>(model.spellProperty(), "resource")
        val costText = stringBinding(resourceCost, resource) {
            if (resourceCost.value == null || resource.value == null) {
                null
            } else {
                "" + resourceCost.value + when (resource.value!!) {
                    ResourceType.MANA -> " Mana"
                    ResourceType.PERCENT_OF_BASE_MANA -> "% of Base Mana"
                    ResourceType.RAGE -> " Rage"
                    ResourceType.ENERGY -> " Energy"
                }
            }
        }
        tooltipCostLabel.visibleProperty().bind(costText.isNotNull)
        tooltipCostLabel.managedProperty().bind(costText.isNotNull)
        tooltipCostLabel.textProperty().bind(costText)

        val range = Bindings.select<Double?>(model.spellProperty(), "range")
        val rangeText = range.stringBinding {
            when {
                it == null -> null
                it <= 0.0 -> ""
                it <= 5.0 -> "Melee Range"
                else -> "$it yd"
            }
        }
        tooltipRangeLabel.visibleProperty().bind(rangeText.isNotNull)
        tooltipRangeLabel.managedProperty().bind(rangeText.isNotNull)
        tooltipRangeLabel.textProperty().bind(rangeText)
        tooltipRangeLabel.alignmentProperty().bind(
            Bindings.`when`(costText.isNotNull)
                .then(Pos.CENTER_RIGHT)
                .otherwise(Pos.CENTER_LEFT))

        val castTime = Bindings.select<Double?>(model.spellProperty(), "castTime")
        val castTimeText = castTime.stringBinding {
            when (it) {
                null -> null
                0.0 -> "Instant"
                else -> {
                    DecimalFormat("##.###").format(it) + " sec cast"
                }
            }
        }
        tooltipCastTimeLabel.visibleProperty().bind(castTimeText.isNotNull)
        tooltipCastTimeLabel.managedProperty().bind(castTimeText.isNotNull)
        tooltipCastTimeLabel.textProperty().bind(castTimeText)

        val cooldown = Bindings.select<Double?>(model.spellProperty(), "cooldown")
        val cooldownUnit = Bindings.select<CooldownUnit?>(model.spellProperty(), "cooldownUnit")
        val cooldownText = stringBinding(cooldown, cooldownUnit) {
            if (cooldown.value == null || cooldownUnit.value == null) {
                null
            } else {
                val cd = DecimalFormat("##.###").format(cooldown.value)
                val unit = when (cooldownUnit.value!!) {
                    CooldownUnit.HOURS -> "hr"
                    CooldownUnit.MINUTES -> "min"
                    CooldownUnit.SECONDS -> "sec"
                }
                "$cd $unit cooldown"
            }
        }
        tooltipCooldownLabel.visibleProperty().bind(cooldownText.isNotNull)
        tooltipCooldownLabel.managedProperty().bind(cooldownText.isNotNull)
        tooltipCooldownLabel.textProperty().bind(cooldownText)
    }

    private fun initDesc() {
        // TODO: format description based on current rank
        tooltipDescLabel.textProperty().bind(model.descriptionProperty())

        val showNextRank = model.rankProperty().greaterThan(0).and(
            model.rankProperty().lessThan(model.maxRankProperty()))
        tooltipNextRankPane.visibleProperty().bind(showNextRank)
        tooltipNextRankPane.managedProperty().bind(showNextRank)

        // TODO: format description based on next rank
        tooltipNextRankDescLabel.textProperty().bind(model.descriptionProperty())
    }

    private fun initFooter() {
        // TODO: get spec name, # points
        tooltipSpecRequiredLabel.isVisible = true
        tooltipSpecRequiredLabel.isManaged = true
        tooltipSpecRequiredLabel.text = "Requires ## Points in Spec Talents"

        val prereqName: ObjectBinding<String?> = Bindings.select(model.prerequisiteProperty(), "name")
        val prereqRank = Bindings.select<Int?>(model.prerequisiteProperty(), "rank")
        val prereqMaxRank = Bindings.select<Int?>(model.prerequisiteProperty(), "maxRank")
        val prereqText = prereqName.stringBinding(prereqRank, prereqMaxRank) { name ->
            if (name == null || prereqRank == null || prereqMaxRank == null) {
                null
            } else if (prereqRank.value == prereqMaxRank.value) {
                null
            } else {
                val remaining = prereqMaxRank.value - prereqRank.value
                "Requires $remaining Points in $name"
            }
        }
        tooltipPrereqRequiredLabel.visibleProperty().bind(prereqText.isNotNull)
        tooltipPrereqRequiredLabel.managedProperty().bind(prereqText.isNotNull)
        tooltipPrereqRequiredLabel.textProperty().bind(prereqText)

        // TODO: use common property for this
        val canAlloc = model.rankProperty().lessThan(model.maxRankProperty())
        tooltipClickToLearnLabel.visibleProperty().bind(canAlloc)
        tooltipClickToLearnLabel.managedProperty().bind(canAlloc)

        val canDealloc = model.rankProperty().greaterThan(0)
        tooltipClickToUnlearnLabel.visibleProperty().bind(canDealloc)
        tooltipClickToUnlearnLabel.managedProperty().bind(canDealloc)
    }

    private fun updateActiveBorder() {
        activeBorderRegion.styleClass.clear()
        if (model.rank >= model.maxRank) {
            activeBorderRegion.styleClass += "maxed-out-border"
        } else if (model.rank > 0) {
            activeBorderRegion.styleClass += "active-border"
        } else {
            // do nothing, style already cleared
        }
    }

    @Suppress("unused")
    @FXML
    private fun onMouseClicked(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            if (model.rank < model.maxRank) {
                model.rank++
            }
        } else if (event.button == MouseButton.SECONDARY) {
            if (model.rank > 0) {
                model.rank--
            }
        }
    }
}