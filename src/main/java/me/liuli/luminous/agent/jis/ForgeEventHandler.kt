package me.liuli.luminous.agent.jis

import me.liuli.luminous.event.EventManager
import me.liuli.luminous.hooks.AdditionalEventDispatcher
import me.liuli.luminous.utils.extension.get
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import wrapped.net.minecraft.client.gui.ScaledResolution
import wrapped.net.minecraft.client.multiplayer.WorldClient

class ForgeEventHandler {
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        AdditionalEventDispatcher.checkKey()
        EventManager.callEvent(me.liuli.luminous.event.TickEvent())
    }

    @SubscribeEvent
    fun onUpdate(event: TickEvent.PlayerTickEvent) {
        EventManager.callEvent(me.liuli.luminous.event.UpdateEvent())
    }

    @SubscribeEvent
    fun onRender2d(event: RenderGameOverlayEvent) {
        EventManager.callEvent(me.liuli.luminous.event.Render2DEvent(ScaledResolution(event.get("resolution")), event.partialTicks))
    }

    @SubscribeEvent
    fun onRender3d(event: RenderWorldLastEvent) {
        EventManager.callEvent(me.liuli.luminous.event.Render3DEvent(event.partialTicks))
    }

    @SubscribeEvent
    fun onWorld(event: WorldEvent) {
        val world = event.get("world")
        EventManager.callEvent(me.liuli.luminous.event.WorldEvent(if(world != null) { WorldClient(world) } else { null }))
    }
}