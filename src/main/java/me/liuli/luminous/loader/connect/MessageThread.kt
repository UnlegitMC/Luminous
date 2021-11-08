package me.liuli.luminous.loader.connect

import com.beust.klaxon.Klaxon
import me.liuli.luminous.Luminous
import me.liuli.luminous.utils.misc.LogUtils
import me.liuli.luminous.utils.misc.logInfo
import java.io.File
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * send messages through UDP can be used to communicate with loader and agent without SYN/ACK handshake and keep alive
 */
class MessageThread : Thread() {
    private val socket = DatagramSocket()
    private val buf = ByteArray(1024) // max size of a UDP packet

    init {
        File(Luminous.dataDir, "SOCKET_${if(Luminous.isAgent) {"CLIENT"} else {"SERVER"}}_PORT").writeBytes(socket.localPort.toString().toByteArray())
    }

    override fun run() {
        while (true) {
            val packet = DatagramPacket(buf, buf.size)
            socket.receive(packet)
            parse(String(packet.data, 0, packet.length))
        }
    }

    fun parse(msg: String) {
        val message = Klaxon().parse<Message>(msg) ?: return

        when(message.type) {
            "cmd" -> println("COMMAND EXEC $message")
            "log" -> {
                val log = Klaxon().parse<LogMessage>(message.data) ?: return
                LogUtils.log(log.log4jLevel, log.message)
            }
        }
    }

    fun send(msg: Message) {
        val json = Klaxon().toJsonString(msg)
        val packet = DatagramPacket(json.toByteArray(), json.length, InetAddress.getLocalHost(),
            File(Luminous.dataDir, "SOCKET_${if(Luminous.isAgent) {"SERVER"} else {"CLIENT"}}_PORT").readText().toInt())
        socket.send(packet)
    }
}