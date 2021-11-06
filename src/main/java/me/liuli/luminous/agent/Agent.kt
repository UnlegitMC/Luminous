package me.liuli.luminous.agent

import javassist.ClassPool
import me.liuli.luminous.Luminous
import me.liuli.luminous.agent.hook.HookManager
import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.utils.jvm.AccessUtils
import java.lang.instrument.Instrumentation
import kotlin.concurrent.thread

object Agent {
    lateinit var instrumentation: Instrumentation
        private set

    @JvmStatic
    fun main(agentArgs: String, instrumentation: Instrumentation) {
        this.instrumentation = instrumentation

        AccessUtils.initByTitle()

        try {
            val mcClass = AccessUtils.getObfClassByName("net.minecraft.client.Minecraft")
//            val mc = mcClass.invokeObfMethod("getMinecraft", "()Lnet/minecraft/client/Minecraft;")
//            println(mcClass.getObfFieldValue("mcDataDir", mc) as File)
            HookManager.hookFunctions.add(object: HookFunction(mcClass) {
                @Hook(target = "runTick!()V", type = HookType.METHOD_ENTER, returnable = true)
                fun runTick(returnInfo: HookReturnInfo) {
                    returnInfo.cancel = Math.random() > 0.5
                    println("Tick ${returnInfo.cancel}")
                }
            })
            HookManager.inject(instrumentation)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}