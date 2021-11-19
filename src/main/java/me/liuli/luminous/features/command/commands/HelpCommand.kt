package me.liuli.luminous.features.command.commands

import me.liuli.luminous.features.command.Command
import me.liuli.luminous.features.command.CommandManager
import me.liuli.luminous.utils.misc.logError
import me.liuli.luminous.utils.misc.logInfo
import me.liuli.luminous.utils.misc.logWarn
import kotlin.math.ceil

class HelpCommand : Command("help", "List all commands") {
    override fun exec(args: Array<String>) {
        val page = if (args.isNotEmpty()) {
            try {
                args[0].toInt()
            } catch (e: NumberFormatException) {
                logError("Invalid page number: ${args[0]}")
                1
            }
        } else { 1 }

        val commands = CommandManager.getCommands()
        val maxPage = ceil(commands.size / 8.0).toInt()
        if(page > maxPage) {
            logError("Page number $page is out of range, max page is $maxPage")
        } else if(page <= 0) {
            logError("Page number $page is out of range, min page is 1")
        } else {
            logWarn("Help ($page/$maxPage)")
            commands.let { it.subList(8 * (page - 1), (8 * page).coerceAtMost(it.size)) }
                .forEach {
                    logInfo("> ${it.command} - ${it.description}")
                }
        }
    }
}