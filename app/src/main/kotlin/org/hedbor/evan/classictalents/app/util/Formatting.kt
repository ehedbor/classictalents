package org.hedbor.evan.classictalents.app.util

import java.text.MessageFormat
import java.util.*


fun ResourceBundle.getAndFormat(key: String, vararg formatArgs: Any?): String
        = MessageFormat(this.getString(key)).format(formatArgs)
