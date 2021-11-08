package me.liuli.luminous.utils.misc

import me.liuli.luminous.Luminous
import me.liuli.luminous.agent.Agent
import me.liuli.luminous.loader.connect.LogMessage
import me.liuli.luminous.loader.connect.Message
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object LogUtils {
    lateinit var logger: Logger
        private set

    init {
        if(!Luminous.isAgent) {
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