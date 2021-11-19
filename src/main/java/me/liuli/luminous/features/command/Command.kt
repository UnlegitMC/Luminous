package me.liuli.luminous.features.command

abstract class Command(val command: String, val description: String) {
    abstract fun exec(args: Array<String>)

    open fun getCompletions(args: Array<String>): List<String> {
        return emptyList()
    }
}