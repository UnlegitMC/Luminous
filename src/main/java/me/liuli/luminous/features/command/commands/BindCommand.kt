package me.liuli.luminous.features.command.commands

import me.liuli.luminous.features.command.Command
import me.liuli.luminous.features.config.ConfigManager
import me.liuli.luminous.features.module.ModuleManager
import me.liuli.luminous.utils.misc.logError
import me.liuli.luminous.utils.misc.logInfo
import org.lwjgl.input.Keyboard

class BindCommand : Command("bind", "Change the trigger key of a module") {
    override fun exec(args: Array<String>) {
        if(args.size < 2) {
            logError("Syntax: $command <module> <key>")
            return
        }

        val module = ModuleManager[args[0]]
        if(module == null) {
            logError("Module ${args[0]} not found")
            return
        }
        val key = try {
            Integer.parseInt(args[1])
        } catch (e: NumberFormatException) {
            Keyboard.getKeyIndex(args[1].uppercase())
        }

        module.keyBind = key
        ConfigManager.scheduleSave()
        logInfo("Set ${module.name}'s key to $${Keyboard.getKeyName(module.keyBind)}")
    }

    override fun getCompletions(args: Array<String>): List<String> {
        return when(args.size) {
            1 -> ModuleManager.getModuleNames()
            else -> emptyList()
        }
    }
}