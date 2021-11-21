package me.liuli.luminous.features.command.commands

import me.liuli.luminous.features.command.Command
import me.liuli.luminous.features.config.ConfigManager
import me.liuli.luminous.features.module.ModuleManager
import me.liuli.luminous.features.module.ModuleTriggerType
import me.liuli.luminous.utils.misc.logError
import me.liuli.luminous.utils.misc.logInfo

class TriggerCommand : Command("trigger", "Change the trigger type of a module") {
    override fun exec(args: Array<String>) {
        if(args.size < 2) {
            logError("Syntax: $command <module> <trigger>")
            return
        }

        val module = ModuleManager[args[0]]
        if(module == null) {
            logError("Module ${args[0]} not found")
            return
        }
        val type = ModuleTriggerType.values().find { it.name.equals(args[1], true) }
        if(type == null) {
            logError("TriggerType ${args[1]} not found")
            return
        }

        module.triggerType = type
        ConfigManager.scheduleSave()
        logInfo("TriggerType of ${module.name} changed to ${type.name}")
    }

    override fun getCompletions(args: Array<String>): List<String> {
        return when(args.size) {
            1 -> ModuleManager.getModuleNames()
            2 -> ModuleTriggerType.values().map { it.name }
            else -> emptyList()
        }
    }
}