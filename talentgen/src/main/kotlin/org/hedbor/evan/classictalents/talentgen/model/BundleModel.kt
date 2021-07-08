package org.hedbor.evan.classictalents.talentgen.model

import tornadofx.ItemViewModel


class BundleModel(initialValue: Bundle? = null) : ItemViewModel<Bundle>(initialValue) {
    val locale = bind(Bundle::localeProperty)
    val entries = bind(Bundle::entriesProperty)
}

class BundleEntryModel(initialValue: BundleEntry? = null) : ItemViewModel<BundleEntry>(initialValue) {
    val translationKey = bind(BundleEntry::translationKeyProperty)
    val displayName = bind(BundleEntry::displayNameProperty)
}