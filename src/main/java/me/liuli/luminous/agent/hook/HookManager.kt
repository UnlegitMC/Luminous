package me.liuli.luminous.agent.hook

import me.liuli.luminous.agent.Agent
import me.liuli.luminous.agent.hook.asm.HookTransformer
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookMethod
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.utils.extension.signature
import me.liuli.luminous.utils.misc.logError
import me.liuli.luminous.utils.misc.logWarn
import java.lang.instrument.Instrumentation

object HookManager {
    private val hookFunctions = mutableListOf<HookFunction>()
    private val hookMethodCache = mutableMapOf<String, HookMethod>()

    private var injected = false

    /**
     * get hook function by [clazz]
     */
    fun getHookFunction(clazz: Class<*>) = hookFunctions.find { it.targetClass == clazz }

    /**
     * register hook functions
     * @throws IllegalStateException if [inject] has been called
     * @throws IllegalArgumentException if another hook function has been registered for the same class
     */
    fun registerHookFunction(hookFunction: HookFunction) {
        if (injected) {
            throw IllegalStateException("Hooks has been injected")
        }
        if (getHookFunction(hookFunction.targetClass) != null) {
            throw IllegalArgumentException("Target class has been registered for ${hookFunction.javaClass.name}")
        }
        hookFunctions.add(hookFunction)
        hookFunction.hookedMethods.forEach {
            hookMethodCache[it.id] = it
        }
    }

    /**
     * inject hook code into target class by [Instrumentation]
     */
    fun inject(instrumentation: Instrumentation) {
        injected = true

        instrumentation.addTransformer(HookTransformer(), true)

        // reload class after transform
        hookFunctions.forEach { function ->
            try {
                instrumentation.retransformClasses(function.targetClass)
            } catch (e: Throwable) {
                logError("Failed to inject hook: ${function.javaClass.name} ($e)")
            }
        }
    }

    /**
     * called from bytecode hooks
     */
    @JvmStatic
    fun invokeHookMethod(hookName: String, instance: Any?, vararg args: Any?) {
        if(!injected) {
            logWarn("Reloading client...")
            Agent.initForForge()
            injected = true
        }
        cache = hookMethodCache[hookName]?.run(instance, *args)
    }

    /**
     * !! THIS FIELD IS NOT THREADSAFE !!
     * cache for hook method return info
     */
    @JvmStatic
    var cache: HookReturnInfo? = HookReturnInfo()
}