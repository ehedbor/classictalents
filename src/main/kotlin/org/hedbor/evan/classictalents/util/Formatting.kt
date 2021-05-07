package org.hedbor.evan.classictalents.util

import java.text.MessageFormat
import java.util.*


fun ResourceBundle.getAndFormat(key: String, vararg formatArgs: Any?): String
        = MessageFormat(getString(key)).format(formatArgs)
