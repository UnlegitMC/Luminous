package me.liuli.luminous.hooks.client

import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.features.ui.panel.ThePanel
import me.liuli.luminous.utils.jvm.AccessUtils

class GuiChatHook : HookFunction(AccessUtils.getObfClass("net.minecraft.client.gui.GuiChat")) {
    @Hook(target = "initGui!()V", type = HookType.METHOD_ENTER)
    fun initGui() {
        ThePanel.inFocus = false
    }

    @Hook(target = "handleMouseInput!()V", type = HookType.METHOD_ENTER, returnable = true)
    fun handleMouseInput(returnInfo: HookReturnInfo) {
        ThePanel.handleMouseInput(returnInfo)
    }

    @Hook(target = "drawScreen!(IIF)V", type = HookType.METHOD_EXIT, getParameters = true)
    fun drawScreen(mouseX: Int, mouseY: Int, pTicks: Float) {
        ThePanel.onRender(mouseX, mouseY, pTicks)
    }

    @Hook(target = "keyTyped!(CI)V", type = HookType.METHOD_ENTER, returnable = true, getParameters = true)
    fun keyTyped(returnInfo: HookReturnInfo, key: Char, keyCode: Int) {
        ThePanel.keyTyped(returnInfo, key, keyCode)
    }

    @Hook(target = "mouseClicked!(III)V", type = HookType.METHOD_ENTER, getParameters = true)
    fun mouseClicked(x: Int, y: Int, button: Int) {
        ThePanel.mouseClicked(x, y, button)
    }
}