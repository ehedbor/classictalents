package org.hedbor.evan.classictalents.app

import org.hedbor.evan.classictalents.app.view.styles.TalentButtonStyles
import org.hedbor.evan.classictalents.app.view.styles.TalentButtonTooltipStyles
import org.hedbor.evan.classictalents.app.view.styles.TalentTreeStyles
import org.hedbor.evan.classictalents.app.view.MainView
import tornadofx.App

class ClassicStatsApp : App(MainView::class, TalentTreeStyles::class, TalentButtonStyles::class, TalentButtonTooltipStyles::class)