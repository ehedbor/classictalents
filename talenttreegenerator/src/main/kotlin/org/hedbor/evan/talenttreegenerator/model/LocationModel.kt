package org.hedbor.evan.talenttreegenerator.model

import tornadofx.ItemViewModel

class LocationModel(initialValue: Location? = null) : ItemViewModel<Location>(initialValue) {
    val row = bind(Location::rowProperty)
    val column = bind(Location::columnProperty)
}