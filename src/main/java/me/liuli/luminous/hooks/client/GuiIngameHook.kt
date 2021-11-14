package me.liuli.luminous.hooks.client

import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.event.EventManager
import me.liuli.luminous.event.Render2DEvent
import me.liuli.luminous.utils.jvm.AccessUtils

class GuiIngameHook : HookFunction(AccessUtils.getObfClassByName("net.minecraft.client.gui.GuiIngame")) {
    @Hook(target = "renderTooltip!(Lnet/minecraft/client/gui/ScaledResolution;F)V", type = HookType.METHOD_EXIT, getParameters = true)
    fun renderTooltip(resolution: Any, partialTicks: Float) {
        EventManager.callEvent(Render2DEvent(partialTicks))
    }
}