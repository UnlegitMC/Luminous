package me.liuli.luminous.agent

import me.liuli.luminous.utils.extension.getObfFieldAccessable
import me.liuli.luminous.utils.extension.getObfFieldValue
import me.liuli.luminous.utils.extension.invokeObfMethod
import me.liuli.luminous.utils.jvm.AccessUtils
import java.io.File
import java.lang.instrument.Instrumentation

object Agent {
    lateinit var instrumentation: Instrumentation
        private set

    fun main(agentArgs: String, instrumentation: Instrumentation) {
        this.instrumentation = instrumentation

        AccessUtils.initByTitle()

        try {
            val mcClass = AccessUtils.getObfClassByName("net.minecraft.client.Minecraft")
            val mc = mcClass.invokeObfMethod("getMinecraft", "()Lnet/minecraft/client/Minecraft;")
            println(mcClass.getObfFieldValue("mcDataDir", mc) as File)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}