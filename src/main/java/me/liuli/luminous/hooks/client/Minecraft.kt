package me.liuli.luminous.hooks.client

import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.utils.jvm.AccessUtils

object Minecraft : HookFunction(AccessUtils.getObfClassByName("net.minecraft.client.Minecraft")) {
    val instance: Any by lazy {
        invokeStatic("getMinecraft","()Lnet/minecraft/client/Minecraft;")!!
    }
}