package me.liuli.luminous.features.command

import me.liuli.luminous.utils.jvm.AccessUtils
import me.liuli.luminous.utils.misc.logWarn

object CommandManager {
    private val commands = HashMap<String, Command>()

    init {
        AccessUtils.resolvePackage("me.liuli.luminous.features.command.commands", Command::class.java)
            .forEach(this::registerCommand)
    }

    private fun registerCommand(clazz: Class<out Command>) {
        registerCommand(AccessUtils.getInstance(clazz))
    }

    fun registerCommand(command: Command) {
        commands[command.command.lowercase()] = command
    }

    fun getCommands() = commands.values.toList()

    fun handleCommand(msg: String) {
        val args = msg.split(" ").toTypedArray()
        val command = commands[args[0]]
        if (command == null) {
            logWarn("Command not found. Type /help in console to view all commands.")
            return
        }

        try {
            command.exec(args.drop(1).toTypedArray())
        } catch (e: Exception) {
            e.printStackTrace()
            logWarn("An error occurred while executing the command($e)")
        }
    }

    fun getCompletions(input: String): Array<String> {
        if (input.isNotEmpty()) {
            val args = input.split(" ")

            return if (args.size > 1) {
                val command = commands[args[0]] ?: return emptyArray()
                val tabCompletions = command.getCompletions(args.drop(1).toTypedArray())

                tabCompletions.toTypedArray()
            } else {
                commands.map { it.key }.toTypedArray()
            }
        }
        return emptyArray()
    }
}