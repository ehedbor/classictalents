package org.hedbor.evan.classictalents.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import org.hedbor.evan.classictalents.util.delegate


@Suppress("MemberVisibilityCanBePrivate", "unused")
class Talent {
    private val _name = SimpleStringProperty("")
    var name: String by _name.delegate()
    fun nameProperty() = _name

    private val _row = SimpleIntegerProperty()
    var row by _row.delegate()
    fun rowProperty() = _row

    private val _column = SimpleIntegerProperty()
    var column by _column.delegate()
    fun columnProperty() = _column

    private val _prerequisite = SimpleObjectProperty<Talent?>()
    var prerequisite by _prerequisite.delegate()
    fun prerequisiteProperty() = _prerequisite

    private val _maxRank = SimpleIntegerProperty()
    var maxRank by _maxRank.delegate()
    fun maxRankProperty() = _maxRank

    private val _rank = SimpleIntegerProperty()
    var rank by _rank.delegate()
    fun rankProperty() = _rank

    // TODO: default image
    private val _icon = SimpleObjectProperty<Image>()
    var icon: Image by _icon.delegate()
    fun iconProperty() = _icon

    private val _description = SimpleStringProperty("")
    var description: String by _description.delegate()
    fun descriptionProperty() = _description

    private val _spell = SimpleObjectProperty<Spell?>()
    var spell by _spell.delegate()
    fun spellProperty() = _spell
}