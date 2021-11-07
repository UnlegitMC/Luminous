package me.liuli.luminous.agent.hook.impl

/**
 * a container for method hooks
 */
abstract class HookFunction(val targetClass: Class<*>) {
    private val hookedMethods = mutableListOf<HookMethod>()

    init {
        this.javaClass.declaredMethods.forEach { method ->
            val hook = method.getAnnotation(Hook::class.java)
            if(hook != null) {
                hookedMethods.add(HookMethod(this, method, hook))
            }
        }
    }

    /**
     * get the hook method by name and signature, return null if not found
     */
    fun getHookMethod(name: String, sign: String): HookMethod? {
        return hookedMethods.find { it.targetMethodName == name && it.targetMethodSign == sign }
    }
}