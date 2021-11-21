package me.liuli.luminous.features.module

import me.liuli.luminous.event.Listener
import me.liuli.luminous.features.config.ConfigManager
import me.liuli.luminous.features.value.Value
import me.liuli.luminous.utils.jvm.AccessUtils
import org.lwjgl.input.Keyboard

open class Module(
    val name: String,
    val description: String,
    val category: ModuleCategory,
    var keyBind: Int = Keyboard.CHAR_NONE,
    var triggerType: ModuleTriggerType = ModuleTriggerType.TOGGLE,
    val canToggle: Boolean = true,
    val defaultOn: Boolean = false
) : Listener {

    val defaultKeyBind = keyBind

    var state = defaultOn
       set(state) {
           if (field == state) return

           if (canToggle) {
               field = state
           }

           if (state) {
               onEnable()
           } else {
               onDisable()
           }
           ConfigManager.scheduleSave()
       }

    fun toggle() {
        state = !state
    }

    open fun onEnable() {}

    open fun onDisable() {}

    val values: List<Value<*>>
        get() = AccessUtils.getValues(this)

    /**
     * get a value but with kotlin feature
     */
    operator fun get(valueName: String) = this.values.find { it.name.equals(valueName, ignoreCase = true) }

    override val listen: Boolean
        get() = state
}