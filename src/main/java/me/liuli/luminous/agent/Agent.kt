package me.liuli.luminous.agent

import me.liuli.luminous.Luminous
import me.liuli.luminous.agent.hook.HookManager
import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.utils.jvm.AccessUtils
import java.lang.instrument.Instrumentation
import java.net.URL
import java.net.URLClassLoader

object Agent {
    lateinit var instrumentation: Instrumentation
        private set

    var forgeEnv = false
        private set

    @JvmStatic
    fun main(agentArgs: String, instrumentation: Instrumentation) {
        this.instrumentation = instrumentation

        AccessUtils.initWithAutoVersionDetect()

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
        }
    }

    private fun loadHooks() {
        val mcClass = AccessUtils.getObfClassByName("net.minecraft.client.Minecraft")
//            val mc = mcClass.invokeObfMethod("getMinecraft", "()Lnet/minecraft/client/Minecraft;")
//            println(mcClass.getObfFieldValue("mcDataDir", mc) as File)
        HookManager.hookFunctions.add(object: HookFunction(mcClass) {
            @Hook(target = "runTick!()V", type = HookType.METHOD_ENTER, returnable = true)
            fun runTick(returnInfo: HookReturnInfo) {
                returnInfo.cancel = Math.random() > 0.8
                println("Tick ${returnInfo.cancel}")
            }
        })
    }

    fun initForForge() {
        forgeEnv = true

        AccessUtils.initWithAutoVersionDetect()

        loadHooks()

        init()
    }

    fun init() {
    }
}