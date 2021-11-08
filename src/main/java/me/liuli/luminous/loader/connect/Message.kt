package me.liuli.luminous.loader.connect

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon

data class Message(@Json(index = 0) val type: String, @Json(index = 1) val data: String) {
    constructor(logMessage: LogMessage) : this("log", Klaxon().toJsonString(logMessage))
}