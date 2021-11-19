package me.liuli.luminous.hooks.other

import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.event.EventManager
import me.liuli.luminous.event.Render3DEvent
import me.liuli.luminous.utils.jvm.AccessUtils

class EntityRendererHook : HookFunction(AccessUtils.getObfClass("net.minecraft.client.renderer.EntityRenderer")) {
    @Hook(target = "renderWorldPass!(IFJ)V", type = HookType.METHOD_EXIT, getParameters = true)
    fun renderWorldPass(pass: Int, partialTicks: Float, finishTimeNano: Long) {
        EventManager.callEvent(Render3DEvent(partialTicks))
    }
}