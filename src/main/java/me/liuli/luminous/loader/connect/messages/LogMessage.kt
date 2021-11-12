package me.liuli.luminous.loader.connect.messages

import com.beust.klaxon.Json
import org.apache.logging.log4j.Level

data class LogMessage(@Json(index = 0) val level: String, @Json(index = 1) val message: String): ISubMessage {
    constructor(level: Level, message: String) : this(level.toString(), message)

    @Json(ignored = true)
    val log4jLevel: Level
        get() = Level.valueOf(level)

    override val type: String
        get() = "log"
}