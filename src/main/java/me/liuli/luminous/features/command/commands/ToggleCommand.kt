package me.liuli.luminous.features.command.commands

import me.liuli.luminous.features.command.Command
import me.liuli.luminous.features.module.ModuleManager
import me.liuli.luminous.utils.misc.logError
import me.liuli.luminous.utils.misc.logWarn

class ToggleCommand : Command("toggle", "Allow you toggle modules without open ClickGui") {
    override fun exec(args: Array<String>) {
        if (args.isNotEmpty()) {
            args.forEach {
                if (it.isBlank()) {
                    return@forEach
                }

                val module = ModuleManager[it]
                if (module == null) {
                    logError("Module \"$it\" not found.")
                } else {
                    module.toggle()
                    logWarn("Toggled module \"${module.name}\" ${if (module.state){"§aON"}else {"§cOFF"}}")
                }
            }
            return
        }
        logError("Syntax: $command <module..>")
    }

    override fun getCompletions(args: Array<String>): List<String> {
        return ModuleManager.getModuleNames()
    }
}