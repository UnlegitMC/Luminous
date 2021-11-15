package me.liuli.luminous.utils.misc

import me.liuli.luminous.utils.misc.LogUtils.log
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.Logger

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

fun logInfo(message: Any) {
    log(Level.INFO, message.toString())
}

fun logWarn(message: Any) {
    log(Level.WARN, message.toString())
}

fun logError(message: Any) {
    log(Level.ERROR, message.toString())
}

fun logDebug(message: Any) {
    log(Level.DEBUG, message.toString())
}