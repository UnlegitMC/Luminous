package me.liuli.luminous.hooks.client

import me.liuli.luminous.Luminous
import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.event.EventManager
import me.liuli.luminous.event.TickEvent
import me.liuli.luminous.event.WorldEvent
import me.liuli.luminous.hooks.AdditionalEventDispatcher
import me.liuli.luminous.utils.jvm.AccessUtils
import wrapped.net.minecraft.client.multiplayer.WorldClient

class MinecraftHook : HookFunction(AccessUtils.getObfClass("net.minecraft.client.Minecraft")) {
    @Hook(target = "runTick!()V", type = HookType.METHOD_ENTER)
    fun tick() {
        AdditionalEventDispatcher.checkKey()
        EventManager.callEvent(TickEvent())
    }

    @Hook(target = "loadWorld!(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", type = HookType.METHOD_ENTER, getParameters = true)
    fun loadWorld(world: Any?, worldName: String) {
        EventManager.callEvent(WorldEvent(if(world != null) { WorldClient(world) } else { null }))
    }

    @Hook(target = "shutdown!()V", type = HookType.METHOD_ENTER)
    fun shutdown() {
        Luminous.shutdown()
    }
}