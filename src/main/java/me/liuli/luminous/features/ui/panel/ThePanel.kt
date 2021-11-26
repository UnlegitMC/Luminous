package me.liuli.luminous.features.ui.panel

import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.event.Render2DEvent
import me.liuli.luminous.utils.game.mc

object ThePanel {
    fun onRender(event: Render2DEvent) {
        mc.fontRendererObj!!.drawString("Hello, world!", 10, 10, 0xFFFFFF)
    }

    fun keyTyped(returnInfo: HookReturnInfo, key: Char, keyCode: Int) {
    }

    fun mouseClicked(x: Int, y: Int, button: Int) {
    }
}