package me.liuli.luminous.loader.console

import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine

class ConsoleCompleter : Completer {
    override fun complete(reader: LineReader, line: ParsedLine, candidates: MutableList<Candidate>) {
        println("LINE ${line.word()}")
        candidates.add(Candidate("test"))
    }
}