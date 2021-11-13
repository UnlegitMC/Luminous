package me.liuli.luminous.utils.misc

import me.liuli.luminous.Luminous
import me.liuli.luminous.agent.Agent
import me.liuli.luminous.loader.connect.Message
import me.liuli.luminous.loader.connect.messages.LogMessage
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.plugins.util.PluginManager

object LogUtils {
    lateinit var logger: Logger
        private set

    init {
        if(!Luminous.isAgent) {
            PluginManager.addPackage("net.minecrell.terminalconsole")
            logger = LogManager.getLogger(Luminous.NAME)
        }
    }

    fun log(level: Level, message: String) {
        if(Luminous.isAgent) {
            Agent.messageThread.send(Message(LogMessage(level, message)))
        } else {
            logger.log(level, message)
        }
    }
}