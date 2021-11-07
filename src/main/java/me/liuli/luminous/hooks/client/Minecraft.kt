package me.liuli.luminous.hooks.client

import me.liuli.luminous.Luminous
import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.utils.extension.invokeObfMethod
import me.liuli.luminous.utils.jvm.AccessUtils

object Minecraft : HookFunction(AccessUtils.getObfClassByName("net.minecraft.client.Minecraft")) {
    val instance: Any by lazy {
        targetClass.invokeObfMethod("getMinecraft","()Lnet/minecraft/client/Minecraft;")
    }

    @Hook(target = "loadWorld!(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", type = HookType.METHOD_ENTER)
    fun loadWorld() {

    }

    @Hook(target = "displayGuiScreen!(Lnet/minecraft/client/gui/GuiScreen;)V", type = HookType.METHOD_ENTER)
    fun displayGuiScreen() {
        
    }

    @Hook(target = "shutdown!()V", type = HookType.METHOD_ENTER)
    fun shutdown() {
        Luminous.shutdown()
    }
}