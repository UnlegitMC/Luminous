package me.liuli.luminous.features.command

import me.liuli.luminous.utils.game.GameUtils

abstract class Command(val command: String, val description: String) {
    abstract fun exec(args: Array<String>)

    open fun getCompletions(args: Array<String>): List<String> {
        return emptyList()
    }
}