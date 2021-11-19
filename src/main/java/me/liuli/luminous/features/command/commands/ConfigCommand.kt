package me.liuli.luminous.features.command.commands

import me.liuli.luminous.features.command.Command
import me.liuli.luminous.features.config.ConfigManager
import me.liuli.luminous.utils.misc.logError
import me.liuli.luminous.utils.misc.logInfo

class ConfigCommand : Command("config", "Manage configs of the client") {
    private val rootSyntax = "load/save/reload/list"

    override fun exec(args: Array<String>) {
        if (args.isEmpty()) {
            logError("Syntax: <$rootSyntax>")
            return
        }

        when (args[0].lowercase()) {
            "load" -> {
                if (args.size > 1) {
                    ConfigManager.switchConfig(args[1])
                } else {
                    logError("Syntax: <ConfigName>")
                }
            }

            "save" -> {
                ConfigManager.writeAll()
                logInfo("Config §l${ConfigManager.configName}§r saved")
            }

            "list" -> {
                logInfo("List of configs:")
                ConfigManager.configDir.listFiles().forEach {
                    val name = it.name.substring(0, it.name.length-5)
                    logInfo("> ${if(name==ConfigManager.configName) { "§c" } else { "§e" }}$name")
                }
            }

            "reload" -> {
                ConfigManager.loadAll()
                logInfo("Config §l${ConfigManager.configName}§r reloaded")
            }

            else -> logError("Syntax: <$rootSyntax>")
        }
    }

    override fun getCompletions(args: Array<String>): List<String> {
        return when(args.size) {
            1 -> rootSyntax.split("/")
            2 -> ConfigManager.configDir.listFiles().map { it.name.substring(0, it.name.length-5) }
            else -> emptyList()
        }
    }
}