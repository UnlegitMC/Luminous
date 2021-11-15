package me.liuli.luminous.loader.connect.messages

class Completions(val result: Array<String>) : ISubMessage {

    override val type: String
        get() = "cmd-complete-resp"
}