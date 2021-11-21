package me.liuli.luminous.features.module.modules.movement

import me.liuli.luminous.event.EventHandler
import me.liuli.luminous.event.UpdateEvent
import me.liuli.luminous.features.module.Module
import me.liuli.luminous.features.module.ModuleCategory
import me.liuli.luminous.utils.extension.get
import me.liuli.luminous.utils.game.mc
import me.liuli.luminous.utils.jvm.AccessUtils
import wrapped.net.minecraft.client.settings.GameSettings

class InventoryMoveModule : Module("InventoryMove", "Allows you to walk while opening the inventory.", ModuleCategory.MOVEMENT) {
    private val guiChatClass = AccessUtils.getObfClass("net.minecraft.client.gui.GuiChat")

    @EventHandler
    private fun onUpdate(event: UpdateEvent) {
        val gameSettings = mc.gameSettings!!
        if(!guiChatClass.isInstance(mc.theInstance.get("currentScreen"))) {
            gameSettings.keyBindForward?.also { it.pressed = GameSettings.isKeyDown(it) }
            gameSettings.keyBindBack?.also { it.pressed = GameSettings.isKeyDown(it) }
            gameSettings.keyBindRight?.also { it.pressed = GameSettings.isKeyDown(it) }
            gameSettings.keyBindLeft?.also { it.pressed = GameSettings.isKeyDown(it) }
            gameSettings.keyBindJump?.also { it.pressed = GameSettings.isKeyDown(it) }
            gameSettings.keyBindSprint?.also { it.pressed = GameSettings.isKeyDown(it) }
        }
    }
}