package me.liuli.luminous

import com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER
import me.liuli.luminous.agent.Agent
import me.liuli.luminous.loader.Loader
import java.io.File
import java.lang.instrument.Instrumentation
import java.net.URL
import java.net.URLClassLoader

object Luminous {
    const val NAME = "Luminous"
    const val VERSION = "1.0.0"

    val dataDir = File(System.getProperty("user.home"), NAME.lowercase())
    val jarFileAt = File(this.javaClass.protectionDomain.codeSource.location.toURI())

    @JvmStatic
    fun main(args: Array<String>) {
        loadClasspaths(args[0]) // load tools.jar into classpath
        Loader.main(args.copyOfRange(1, args.size))
    }

    @JvmStatic
    fun agentmain(agentArgs: String?, inst: Instrumentation) {
        Agent.main(agentArgs ?: "", inst)
    }

    private fun loadClasspaths(paths: String) {
        for (path in paths.split(":").toTypedArray()) {
            try {
                loadClasspath(File(path))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    /**
     * idk why classpath option cannot load tools.jar
     * @param jar the jar file want to load
     */
    private fun loadClasspath(jar: File) {
        val classLoaderExt = this.javaClass.classLoader as URLClassLoader
        val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
        method.isAccessible = true
        method.invoke(classLoaderExt, jar.toURI().toURL())
    }
}