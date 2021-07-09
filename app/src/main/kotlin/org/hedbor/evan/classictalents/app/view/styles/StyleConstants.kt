package org.hedbor.evan.classictalents.app.view.styles

import javafx.scene.paint.Color
import tornadofx.Dimension
import tornadofx.px


object StyleConstants {
    val LARGE_LABEL_TEXT: Dimension<Dimension.LinearUnits> = 20.px
    val SMALL_LABEL_TEXT: Dimension<Dimension.LinearUnits> = 16.px
    const val UI_FONT: String = "Friz Quadrata TT"
    val NORMAL_COLOR: Color = Color.WHITE
    val DESCRIPTION_COLOR: Color = Color.GOLD
    val ERROR_COLOR: Color = Color.RED
    val CONFIRMATION_COLOR: Color = Color.LIMEGREEN

    val TALENT_ACTIVE_BORDER_COLOR = Color(64.0 / 255.0, 191.0 / 255.0, 64.0 / 255.0, 0.8)
    val TALENT_MAXED_BORDER_COLOR = Color(255.0 / 255.0, 209.0 / 255.0, 0.0 / 255.0, 0.8)
}