package me.liuli.luminous

import me.liuli.luminous.agent.Agent
import me.liuli.luminous.loader.Loader
import me.liuli.luminous.utils.misc.logError
import me.liuli.luminous.utils.misc.logInfo
import me.liuli.luminous.utils.misc.logWarn
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
        // load tools.jar into classpath
        loadClasspath(if(System.getProperty("luminous.toolsjar") != null) {
            val toolsJar = File(System.getProperty("luminous.toolsjar"))
            if(toolsJar.exists()) {
                logWarn("Load Tools.jar from property: ${toolsJar.absolutePath}")
                toolsJar
            } else {
                logError("Tools.jar not exists: ${toolsJar.absolutePath}")
                return
            }
        } else {
            val toolsJar = getToolsJar()
            if(toolsJar == null) {
                logError("Failed to find Tools.jar, Please add an property with name \"luminous.toolsjar\" points tools.jar in jvm.")
                return
            }
            logInfo("Tools.jar was found automatically: ${toolsJar.absolutePath}")
            toolsJar
        })

        Loader.main()
    }

    @JvmStatic
    fun agentmain(agentArgs: String?, inst: Instrumentation) {
        Agent.main(agentArgs ?: "", inst)
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

    /**
     * get tools jar by java.home
     */
    private fun getToolsJar(): File? {
        val javaHome = File(System.getProperty("java.home"))
        val toolsJar = File(javaHome, "lib/tools.jar")

        // if java.home is jdk
        if (toolsJar.exists()) {
            return toolsJar
        }

        // if java.home is jre
        if(javaHome.name.equals("jre", ignoreCase = true)) {
            File(javaHome.parentFile, "lib/tools.jar").also {
                if(it.exists())
                    return it
            }
        }

        return null
    }
}