package me.liuli.luminous.agent.hook.impl

annotation class Hook(val target: String, val type: HookType, val getInstance: Boolean = false, val returnable: Boolean = false)

enum class HookType {
    METHOD_ENTER,
    METHOD_EXIT
}

class HookReturnInfo(var cancel: Boolean = false, var returnValue: Any? = null) {
    fun cancel(value: Any?) {
        cancel = true
        returnValue = value
    }
}
