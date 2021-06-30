package org.hedbor.evan.classictalents.talentgen.model

import org.hedbor.evan.classictalents.common.model.Location
import tornadofx.*

class LocationModel(initialValue: Location? = null) : ItemViewModel<Location>(initialValue) {
    val row = bind(Location::rowProperty)
    val column = bind(Location::columnProperty)
}