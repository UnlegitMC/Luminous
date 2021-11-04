package me.liuli.luminous.utils.misc

import me.liuli.luminous.Luminous

fun log(tag: String, message: String) {
    println("[${Luminous.NAME}] [$tag] $message")
}

fun logInfo(message: String) {
    log("INFO", message)
}

fun logWarn(message: String) {
    log("WARN", message)
}

fun logError(message: String) {
    log("ERROR", message)
}

fun logDebug(message: String) {
    log("DEBUG", message)
}