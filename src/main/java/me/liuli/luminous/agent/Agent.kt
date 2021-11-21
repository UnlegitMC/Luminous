package me.liuli.luminous.agent

import me.liuli.luminous.Luminous
import me.liuli.luminous.agent.hook.HookManager
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.loader.connect.MessageThread
import me.liuli.luminous.utils.jvm.AccessUtils
import me.liuli.luminous.utils.misc.logError
import java.lang.instrument.Instrumentation
import java.net.URL
import java.net.URLClassLoader

object Agent {
    lateinit var instrumentation: Instrumentation
        private set

    var forgeEnv = false

    val messageThread = MessageThread()

    /**
     * called from [Luminous.agentmain]
     */
    fun main(agentArgs: String, instrumentation: Instrumentation) {
        this.instrumentation = instrumentation
        messageThread.start()

        AccessUtils.initWithAutoVersionDetect()

        // forge wrapped the namespace, so we need to use reflection to get the real classloader and load the classes into real namespace
        var forgeFlag = false

        try {
            val classLoader = AccessUtils.getClassByName("net.minecraftforge.fml.common.Loader").classLoader
//            val classLoader = clazz.getDeclaredMethod("getModClassLoader")
//                .invoke(clazz.getDeclaredMethod("instance").invoke(null)) as ClassLoader

            val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
            method.isAccessible = true
            method.invoke(classLoader, Luminous.jarFileAt.toURI().toURL())

            forgeFlag = true
        } catch (ignore: ClassNotFoundException) { } catch (t: Throwable) { t.printStackTrace() }

        loadHooks()
        HookManager.inject(instrumentation)

        if(!forgeFlag) {
            init()
        } else if(messageThread.isAlive) {
            messageThread.interrupt()
        }
    }

    /**
     * load hooks for the cheat
     */
    private fun loadHooks() {
        AccessUtils.resolvePackage("me.liuli.luminous.hooks", HookFunction::class.java)
            .forEach {
                try {
                    HookManager.registerHookFunction(AccessUtils.getInstance(it))
                } catch (e: Throwable) {
                    logError("Failed to load hook: ${it.name} (${e.javaClass.name}: ${e.message})")
                }
            }
    }

    /**
     * called for namespace switch for forge
     */
    fun initForForge() {
        forgeEnv = true
        messageThread.start()

        AccessUtils.initWithAutoVersionDetect()

        loadHooks()

        init()
    }

    /**
     * final initialization for the cheat
     */
    fun init() {
        Luminous.launch()
    }
}