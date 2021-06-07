package org.hedbor.evan.talenttreegenerator.model

import tornadofx.ItemViewModel

class LocationModel : ItemViewModel<Location>() {
    val row = bind(Location::rowProperty)
    val column = bind(Location::columnProperty)
}