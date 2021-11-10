package me.liuli.luminous.utils.misc

import me.liuli.luminous.utils.misc.LogUtils.log
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.Logger
import java.text.SimpleDateFormat

private val DATE_FORMAT = SimpleDateFormat("HH:mm:ss")
lateinit var logger: Logger

fun logInfo(message: String) {
    log(Level.INFO, message)
}

fun logWarn(message: String) {
    log(Level.WARN, message)
}

fun logError(message: String) {
    log(Level.ERROR, message)
}

fun logDebug(message: String) {
    log(Level.DEBUG, message)
}