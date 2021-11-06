package me.liuli.luminous.agent.hook

import me.liuli.luminous.agent.hook.asm.HookTransformer
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.utils.jvm.AccessUtils
import java.lang.instrument.Instrumentation

object HookManager {
    val hookFunctions = mutableListOf<HookFunction>()

    fun getHookFunction(clazz: Class<*>) = hookFunctions.find { it.targetClass == clazz }

    fun inject(instrumentation: Instrumentation) {
        instrumentation.addTransformer(HookTransformer(), true)

        // reload class after transform
        hookFunctions.forEach { function ->
            instrumentation.retransformClasses(function.targetClass)
        }
    }

    @JvmStatic
    fun invokeHookMethod(functionName: String, methodName: String, methodSign: String, instance: Any, vararg args: Any?) {
        cache = getHookFunction(AccessUtils.getClassByName(functionName))?.getHookMethod(methodName, methodSign)?.run(instance, *args)
    }

    @JvmStatic
    var cache: HookReturnInfo? = HookReturnInfo()
}