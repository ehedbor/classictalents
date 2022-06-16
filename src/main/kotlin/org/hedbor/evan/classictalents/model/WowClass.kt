package org.hedbor.evan.classictalents.model

import javafx.beans.property.ReadOnlyIntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
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

    private val _specializations = SimpleListProperty<Specialization>(
        FXCollections.observableArrayList { arrayOf(it.allocatedPointsProperty()) })
    var specializations: ObservableList<Specialization> by _specializations.delegate()
    fun specializationsProperty() = _specializations

    private val _allocatedPoints = SimpleIntegerProperty().apply {
        bind(specializationsProperty().intBinding { specs ->
            specs.sumOf { it.allocatedPoints }
        })
    }
    val allocatedPoints by _allocatedPoints.delegate()
    fun allocatedPointsProperty() = _allocatedPoints as ReadOnlyIntegerProperty
}