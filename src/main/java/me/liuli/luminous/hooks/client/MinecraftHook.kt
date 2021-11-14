package me.liuli.luminous.hooks.client

import me.liuli.luminous.Luminous
import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.event.EventManager
import me.liuli.luminous.event.KeyEvent
import me.liuli.luminous.event.TickEvent
import me.liuli.luminous.event.WorldEvent
import me.liuli.luminous.utils.jvm.AccessUtils
import org.lwjgl.input.Keyboard
import wrapped.net.minecraft.client.multiplayer.WorldClient

class MinecraftHook : HookFunction(AccessUtils.getObfClassByName("net.minecraft.client.Minecraft")) {
    @Hook(target = "runTick!()V", type = HookType.METHOD_ENTER)
    fun tick() {
        if(Keyboard.getEventKeyState()) {
            EventManager.callEvent(KeyEvent(if (Keyboard.getEventKey() == 0) { Keyboard.getEventCharacter().code + 256 } else { Keyboard.getEventKey() }))
        }
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