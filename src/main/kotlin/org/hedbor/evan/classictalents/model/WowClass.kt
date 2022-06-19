package org.hedbor.evan.classictalents.model

import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.image.Image
import javafx.scene.paint.Color
import org.hedbor.evan.classictalents.util.*

@Suppress("MemberVisibilityCanBePrivate")
class WowClass {
    private val _name = SimpleStringProperty("")
    var name: String by _name.delegate()
    fun nameProperty() = _name

    // TODO: default icon
    private val _icon = SimpleObjectProperty<Image>()
    var icon: Image by _icon.delegate()
    fun iconProperty() = _icon

    private val _color = SimpleObjectProperty<Color>(Color.MAGENTA)
    var color: Color by _color.delegate()
    fun colorProperty() = _color

    /*
     * Calculated properties
     */

    private val _specializations = ReadOnlyListWrapper<Specialization>(
        FXCollections.observableArrayList { arrayOf(it.allocatedPointsProperty()) })
    var specializations: ObservableList<Specialization> by _specializations.delegate()
    fun specializationsProperty() = _specializations.readOnlyProperty!!

    private val _allocatedPointsBinding = Bindings.createIntegerBinding({ specializations.sumOf { it.allocatedPoints } }, specializationsProperty())
    private val _allocatedPoints = ReadOnlyIntegerWrapper().also { it.bind(_allocatedPointsBinding) }
    val allocatedPoints by _allocatedPoints.delegate()
    fun allocatedPointsProperty() = _allocatedPoints.readOnlyProperty!!

    // TODO: max points depends on expansion
    private val _maxPoints = ReadOnlyIntegerWrapper(51)
    val maxPoints by _maxPoints.delegate()
    fun maxPointsProperty() = _maxPoints.readOnlyProperty!!

    private val _hasUnassignedPointsBinding = allocatedPointsProperty().lessThan(maxPointsProperty())
    private val _hasUnassignedPoints = ReadOnlyBooleanWrapper().also { it.bind(_hasUnassignedPointsBinding) }
    val hasUnassignedPoints by _hasUnassignedPoints.delegate()
    fun hasUnassignedPointsProperty() = _hasUnassignedPoints.readOnlyProperty!!
}