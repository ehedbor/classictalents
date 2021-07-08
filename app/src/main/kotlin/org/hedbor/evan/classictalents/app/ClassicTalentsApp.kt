package org.hedbor.evan.classictalents.app

import org.hedbor.evan.classictalents.app.view.MainView
import org.hedbor.evan.classictalents.app.view.styles.SpecStyles
import org.hedbor.evan.classictalents.app.view.styles.TalentStyles
import org.hedbor.evan.classictalents.app.view.styles.TalentTooltipStyles
import tornadofx.App

class ClassicTalentsApp : App(MainView::class, SpecStyles::class, TalentStyles::class, TalentTooltipStyles::class)