package me.liuli.luminous.agent.hook

import me.liuli.luminous.agent.Agent
import me.liuli.luminous.agent.hook.asm.HookTransformer
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.utils.jvm.AccessUtils
import me.liuli.luminous.utils.misc.logWarn
import java.lang.instrument.Instrumentation

object HookManager {
    val hookFunctions = mutableListOf<HookFunction>()

    private var injected = false

    fun getHookFunction(clazz: Class<*>) = hookFunctions.find { it.targetClass == clazz }

    fun inject(instrumentation: Instrumentation) {
        instrumentation.addTransformer(HookTransformer(), true)

        // reload class after transform
        hookFunctions.forEach { function ->
            instrumentation.retransformClasses(function.targetClass)
        }

        injected = true
    }

    @JvmStatic
    fun invokeHookMethod(functionName: String, methodName: String, methodSign: String, instance: Any, vararg args: Any?) {
        if(!injected) {
            logWarn("Reloading client...")
            Agent.initForForge()
            injected = true
        }
        cache = getHookFunction(AccessUtils.getClassByName(functionName))?.getHookMethod(methodName, methodSign)?.run(instance, *args)
    }

    @JvmStatic
    var cache: HookReturnInfo? = HookReturnInfo()
}