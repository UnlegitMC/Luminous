package me.liuli.luminous.agent.hook.impl

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

    fun getHookMethod(name: String, sign: String): HookMethod? {
        return hookedMethods.find { it.targetMethodName == name && it.targetMethodSign == sign }
    }
}