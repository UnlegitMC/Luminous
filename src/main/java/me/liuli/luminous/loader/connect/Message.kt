package me.liuli.luminous.loader.connect

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import me.liuli.luminous.loader.connect.messages.ISubMessage
import me.liuli.luminous.loader.connect.messages.LogMessage

data class Message(@Json(index = 0) val type: String, @Json(index = 1) val data: String) {
    constructor(subMessage: ISubMessage) : this(subMessage.type, Klaxon().toJsonString(subMessage))
}