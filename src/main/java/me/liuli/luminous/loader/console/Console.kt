package me.liuli.luminous.loader.console

import me.liuli.luminous.Luminous
import me.liuli.luminous.loader.Loader
import me.liuli.luminous.loader.connect.Message
import me.liuli.luminous.utils.misc.logInfo
import net.minecrell.terminalconsole.SimpleTerminalConsole
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder

class Console : SimpleTerminalConsole() {
    override fun isRunning() = true

    override fun runCommand(command: String) {
        if(Loader.listeningConsole) {
            Loader.consoleMessage = command
            logInfo("EZ $command")
            return
        }
        if(command.lowercase() == "stop") {
            Loader.shutdownLoader()
            return
        }
        Loader.messageThread.send(Message("cmd", command))
    }

    override fun shutdown() {
        // TODO: unhook the client while shutdown
        Loader.shutdownLoader()
    }

    override fun buildReader(builder: LineReaderBuilder): LineReader {
        return super.buildReader(builder
            .appName(Luminous.NAME)
            .completer(ConsoleCompleter()))
    }
}