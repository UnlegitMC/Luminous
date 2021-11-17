package me.liuli.luminous.loader.console

import me.liuli.luminous.Luminous
import me.liuli.luminous.loader.Loader
import me.liuli.luminous.loader.connect.Message
import net.minecrell.terminalconsole.SimpleTerminalConsole
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder

class Console : SimpleTerminalConsole() {
    override fun isRunning() = true

    override fun runCommand(command: String) {
        if(Luminous.listeningConsole) {
            Luminous.consoleMessage = command
            return
        }
        if(command.lowercase() == "stop") {
            Loader.shutdownLoader()
            return
        } else if(command.lowercase() == "inject") {
            Loader.doInject()
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
            .completer(ConsoleCompleter))
    }
}