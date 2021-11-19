package me.liuli.luminous.hooks

import me.liuli.luminous.event.EventManager
import me.liuli.luminous.event.KeyEvent
import me.liuli.luminous.utils.extension.get
import me.liuli.luminous.utils.game.mc
import org.lwjgl.input.Keyboard

object AdditionalEventDispatcher {
    fun checkKey() {
        if(Keyboard.getEventKeyState() && mc.theInstance.get("currentScreen") == null) {
            EventManager.callEvent(KeyEvent(if (Keyboard.getEventKey() == 0) { Keyboard.getEventCharacter().code + 256 } else { Keyboard.getEventKey() }))
        }
    }
}