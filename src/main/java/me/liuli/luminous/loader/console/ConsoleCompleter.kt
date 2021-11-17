package me.liuli.luminous.loader.console

import me.liuli.luminous.loader.Loader
import me.liuli.luminous.loader.connect.Message
import me.liuli.luminous.utils.misc.logError
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine

object ConsoleCompleter : Completer {
    var completionsPending: Array<String>? = null

    override fun complete(reader: LineReader, line: ParsedLine, candidates: MutableList<Candidate>) {
        Loader.messageThread.send(Message("cmd-complete-req", line.line()))
        completionsPending = null
        var count = 0
        while (completionsPending == null) {
            Thread.sleep(100)
            count++
            if (count > 10) {
                logError("Failed to fetch command completions")
                return
            }
        }
        completionsPending!!.forEach {
            candidates.add(Candidate(it))
        }
        candidates.add(Candidate("stop"))
        candidates.add(Candidate("inject"))
    }
}