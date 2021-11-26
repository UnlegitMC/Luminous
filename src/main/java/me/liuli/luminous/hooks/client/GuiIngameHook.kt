package me.liuli.luminous.hooks.client

import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.event.EventManager
import me.liuli.luminous.event.Render2DEvent
import me.liuli.luminous.utils.jvm.AccessUtils
import wrapped.net.minecraft.client.gui.ScaledResolution

class GuiIngameHook : HookFunction(AccessUtils.getObfClass("net.minecraft.client.gui.GuiIngame")) {
    @Hook(target = "renderTooltip!(Lnet/minecraft/client/gui/ScaledResolution;F)V", type = HookType.METHOD_EXIT, getParameters = true)
    fun renderTooltip(resolution: Any, partialTicks: Float) {
        EventManager.callEvent(Render2DEvent(ScaledResolution(resolution), partialTicks))
    }
}