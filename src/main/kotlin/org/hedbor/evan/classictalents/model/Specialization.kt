package org.hedbor.evan.classictalents.model

import javafx.beans.Observable
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.image.Image
import org.hedbor.evan.classictalents.util.delegate
import org.hedbor.evan.classictalents.util.intBinding


@Suppress("MemberVisibilityCanBePrivate", "unused")
class Specialization {
    private val _wowClass = SimpleObjectProperty<WowClass>()
    var wowClass: WowClass by _wowClass.delegate()
    fun wowClassProperty() = _wowClass

    private val _name = SimpleStringProperty("")
    var name: String by _name.delegate()
    fun nameProperty() = _name

    // TODO: default images
    private val _icon = SimpleObjectProperty<Image>()
    var icon: Image by _icon.delegate()
    fun iconProperty() = _icon

    private val _background = SimpleObjectProperty<Image>()
    var background: Image by _background.delegate()
    fun backgroundProperty() = _background

    private val _talents = SimpleListProperty<Talent>(
        FXCollections.observableArrayList { arrayOf(it.rankProperty()) })
    var talents: ObservableList<Talent> by _talents.delegate()
    fun talentsProperty() = _talents

    /*
     * Computed properties:
     */

    private val _allocatedPointsBinding = talentsProperty().intBinding { talents ->
        talents.sumOf { it.rank }
    }
    private val _allocatedPoints = ReadOnlyIntegerWrapper().also { it.bind(_allocatedPointsBinding) }
    val allocatedPoints by _allocatedPoints.delegate()
    fun allocatedPointsProperty() = _allocatedPoints.readOnlyProperty!!
}