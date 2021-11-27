package me.liuli.luminous.agent.hook.impl

import me.liuli.luminous.utils.extension.signature
import me.liuli.luminous.utils.jvm.AccessUtils
import java.lang.reflect.Method

class HookMethod(val hookFunction: HookFunction, val method: Method, val info: Hook) {
    val targetMethodName: String
    val targetMethodSign: String
    val targetMethod: Method
    val id: String

    init {
        val deobf = (AccessUtils.methodOverrideMap[hookFunction.targetClass.name + "!" + info.target]
            ?: (hookFunction.targetClass.name + "!" + info.target)).split("!")
        targetMethodName = deobf[1]
        targetMethodSign = deobf[2]
        targetMethod = AccessUtils.getObfMethod(hookFunction.targetClass, targetMethodName, targetMethodSign)
        id = "${hookFunction.javaClass.name}/${method.name}!${method.signature}"
    }

    /**
     * called when target method hook point is called
     */
    fun run(instance: Any?, vararg args: Any?): HookReturnInfo {
        val invokeArgs = mutableListOf<Any?>()
        val returnInfo = HookReturnInfo()

        if(info.getInstance) {
            invokeArgs.add(instance)
        }
        if(info.returnable) {
            invokeArgs.add(returnInfo)
        }
        if(info.getParameters) {
            args.forEach { invokeArgs.add(it) }
        }

        method.isAccessible = true
        method.invoke(hookFunction, *invokeArgs.toTypedArray())

        return returnInfo
    }
}