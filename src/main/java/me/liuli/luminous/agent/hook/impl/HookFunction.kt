package me.liuli.luminous.agent.hook.impl

/**
 * a container for method hooks
 */
abstract class HookFunction(val targetClass: Class<*>) {
    val hookedMethods = mutableListOf<HookMethod>()

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
    fun getHookMethods(name: String, sign: String): List<HookMethod> {
        return hookedMethods.filter { it.targetMethodName == name && it.targetMethodSign == sign }
    }
}