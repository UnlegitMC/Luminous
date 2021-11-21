package me.liuli.luminous.features.command.commands

import me.liuli.luminous.features.command.Command
import me.liuli.luminous.features.config.ConfigManager
import me.liuli.luminous.features.module.ModuleManager
import me.liuli.luminous.features.value.*
import me.liuli.luminous.utils.misc.logError
import me.liuli.luminous.utils.misc.logInfo

class SetCommand : Command("set", "Change a value of a module") {
    override fun exec(args: Array<String>) {
        if(args.size < 2) {
            logError("Syntax: $command <module> <value>")
            return
        }

        val module = ModuleManager[args[0]]
        if(module == null) {
            logError("Module ${args[0]} not found")
            return
        }
        val value = module[args[1]]
        if(value == null) {
            logError("Value ${args[1]} not found")
            return
        }

        if(value is BoolValue) {
            value.set(args.getOrNull(2)?.toBoolean() ?: !value.value)
        } else if (value is FloatValue) {
            value.set(args.getOrNull(2)?.toFloat() ?: value.defaultValue)
        } else if (value is IntValue) {
            value.set(args.getOrNull(2)?.toInt() ?: value.defaultValue)
        } else if (value is ListValue || value is StringValue) {
            (value as Value<String>).set(args.getOrNull(2) ?: value.defaultValue)
        } else {
            logError("Value ${args[1]} is not supported")
            return
        }

        ConfigManager.scheduleSave()
        logInfo("Set ${value.name} in module ${module.name} to ${value.value}")
    }
}