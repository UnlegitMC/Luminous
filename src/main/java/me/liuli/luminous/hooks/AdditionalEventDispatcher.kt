package me.liuli.luminous.hooks

import me.liuli.luminous.event.EventManager
import me.liuli.luminous.event.KeyEvent
import org.lwjgl.input.Keyboard

object AdditionalEventDispatcher {
    fun checkKey() {
        if(Keyboard.getEventKeyState()) {
            EventManager.callEvent(KeyEvent(if (Keyboard.getEventKey() == 0) { Keyboard.getEventCharacter().code + 256 } else { Keyboard.getEventKey() }))
        }
    }
}