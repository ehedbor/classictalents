package org.hedbor.evan.classictalents

import org.hedbor.evan.classictalents.styles.TalentButtonStyles
import org.hedbor.evan.classictalents.styles.TalentButtonTooltipStyles
import org.hedbor.evan.classictalents.styles.TalentTreeStyles
import tornadofx.App

class ClassicStatsApp : App(MainView::class, TalentTreeStyles::class, TalentButtonStyles::class, TalentButtonTooltipStyles::class)