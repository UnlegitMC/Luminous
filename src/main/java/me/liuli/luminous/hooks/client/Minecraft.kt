package me.liuli.luminous.hooks.client

import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.utils.extension.invokeObfMethod
import me.liuli.luminous.utils.jvm.AccessUtils
import me.liuli.luminous.utils.misc.logInfo

object Minecraft : HookFunction(AccessUtils.getObfClassByName("net.minecraft.client.Minecraft")) {
    val instance = targetClass.invokeObfMethod("getMinecraft","()Lnet/minecraft/client/Minecraft;")!!

    @Hook(target = "runTick!()V", type = HookType.METHOD_ENTER)
    fun shutdown() {
//        logInfo("TICK")
    }

    @Hook(target = "loadWorld!(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", type = HookType.METHOD_ENTER)
    fun loadWorld() {
        logInfo("WORLD")
    }
}