package me.liuli.luminous.loader.connect

import com.beust.klaxon.Klaxon
import me.liuli.luminous.Luminous
import me.liuli.luminous.features.command.CommandManager
import me.liuli.luminous.loader.connect.messages.Completions
import me.liuli.luminous.loader.connect.messages.LogMessage
import me.liuli.luminous.loader.console.ConsoleCompleter
import me.liuli.luminous.utils.misc.LogUtils
import java.io.File
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.charset.StandardCharsets

/**
 * send messages through UDP can be used to communicate with loader and agent without SYN/ACK handshake and keep alive
 */
class MessageThread : Thread() {
    private val socket = DatagramSocket()

    /**
     * cache for smart packet size protocol, -1 for next packet is size packet
     */
    private var nextSize = -1

    init {
        File(Luminous.cacheDir, "SOCKET_${if(Luminous.isAgent) {"CLIENT"} else {"SERVER"}}_PORT").writeBytes(socket.localPort.toString().toByteArray())
    }

    override fun run() {
        while (true) {
            val buf = ByteArray(if(nextSize == -1) { 32 } else { nextSize })
            val packet = DatagramPacket(buf, buf.size)
            socket.receive(packet)
            val packetStr = packet.data.toString(StandardCharsets.UTF_8)
            nextSize = try {
                if(nextSize == -1) {
                    packetStr.split("|")[0].toInt()
                } else {
                    parse(packetStr)
                    // next packet must be size packet
                    -1
                }
            } catch (e: Exception) {
                e.printStackTrace()
                -1
            }
        }
    }

    fun parse(msg: String) {
        val message = Klaxon().parse<Message>(msg) ?: return

        when(message.type) {
            "cmd" -> CommandManager.handleCommand(message.content)
            "cmd-complete-req" -> send(Message(Completions(CommandManager.getCompletions(message.content))))
            "cmd-complete-resp" -> {
                val completions = Klaxon().parse<Completions>(message.content) ?: return
                ConsoleCompleter.completionsPending = completions.result
            }
            "log" -> {
                val log = Klaxon().parse<LogMessage>(message.content) ?: return
                LogUtils.log(log.log4jLevel, log.message)
            }
        }
    }

    fun send(msg: Message) {
        val json = Klaxon().toJsonString(msg).toByteArray()
        val host = InetAddress.getLocalHost()
        val port = File(Luminous.cacheDir, "SOCKET_${if(Luminous.isAgent) {"SERVER"} else {"CLIENT"}}_PORT").readText().toInt()
        json.size.also {
            val buf = "$it|".toByteArray()
            val packet = DatagramPacket(buf, buf.size, host, port)
            socket.send(packet)
        }
        val packet = DatagramPacket(json, json.size, host, port)
        socket.send(packet)
    }
}