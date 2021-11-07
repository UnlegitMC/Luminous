package me.liuli.luminous.agent.hook.impl

/**
 * for methods need to hook
 * method parameters should be (instance, returnInfo, vararg originalMethodParameters)
 *
 * @param target the target method (NAME + ! + SIGNATURE)
 * @param type the hook position
 * @param getParameters post the target method parameters (nullable)
 * @param getInstance post the instance back in hook method parameter (null for static method)
 * @param returnable give a selector to return the value back or not
 */
annotation class Hook(val target: String, val type: HookType, val getParameters: Boolean = false, val getInstance: Boolean = false, val returnable: Boolean = false)

/**
 * select which position to inject the hook bytecode in MethodVisitor
 */
enum class HookType {
    /**
     * inject the hook bytecode when onMethodEnter
     */
    METHOD_ENTER,

    /**
     * inject the hook bytecode when onMethodExit
     */
    METHOD_EXIT
}

/**
 * a container for hook returnable information
 */
class HookReturnInfo(var cancel: Boolean = false, var returnValue: Any? = null) {
    fun `return`(value: Any?) {
        cancel = true
        returnValue = value
    }

    fun `return`() {
        cancel = true
    }
}
