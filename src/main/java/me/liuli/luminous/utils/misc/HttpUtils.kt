package me.liuli.luminous.utils.misc

import java.io.DataOutputStream
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files

/**
 * LiquidBounce Hacked Client
 * A minecraft forge injection client using Mixin
 *
 * @game Minecraft
 * @author CCBlueX
 */
object HttpUtils {

    private const val DEFAULT_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36"

    init {
        HttpURLConnection.setFollowRedirects(true)
    }

    fun make(
        url: String,
        method: String,
        data: String = "",
        agent: String = DEFAULT_AGENT
    ): HttpURLConnection {
        val httpConnection = URL(url).openConnection() as HttpURLConnection

        httpConnection.requestMethod = method
        httpConnection.connectTimeout = 2000
        httpConnection.readTimeout = 10000

        httpConnection.setRequestProperty("User-Agent", agent)

        httpConnection.instanceFollowRedirects = true
        httpConnection.doOutput = true

        if (data.isNotEmpty()) {
            val dataOutputStream = DataOutputStream(httpConnection.outputStream)
            dataOutputStream.writeBytes(data)
            dataOutputStream.flush()
        }

        return httpConnection
    }

    fun request(
        url: String,
        method: String,
        data: String = "",
        agent: String = DEFAULT_AGENT
    ): String {
        val connection = make(url, method, data, agent)

        return connection.inputStream.reader(Charsets.UTF_8).readText()
    }

    fun requestStream(
        url: String,
        method: String,
        agent: String = DEFAULT_AGENT
    ): InputStream? {
        val connection = make(url, method, agent)

        return connection.inputStream
    }

    fun download(url: String, file: File) {
        logInfo("Downloading $url to ${file.absolutePath}")
        Files.copy(make(url, "GET").inputStream, file.toPath())
    }

    // download file if file not exists
    fun downloadIfNotExists(url: String, file: File) {
        if (!file.exists()) {
            download(url, file)
        }
    }

    fun get(url: String) = request(url, "GET")

    fun post(url: String, data: String) = request(url, "POST", data = data)
}
