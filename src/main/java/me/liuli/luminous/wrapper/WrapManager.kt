package me.liuli.luminous.wrapper

import me.liuli.luminous.utils.extension.getMethodByName
import me.liuli.luminous.utils.jvm.AccessUtils

object WrapManager {
    fun access(vararg args: Any?, instance: Any? = null): Any? {
        var element: StackTraceElement? = null
        val iterator = Thread.currentThread().stackTrace.iterator()
        var hasWrapManager = false
        while (iterator.hasNext() && element == null) {
            val stackTraceElement = iterator.next()
            if(hasWrapManager && stackTraceElement.className != this.javaClass.name) {
                element = stackTraceElement
            } else if (stackTraceElement.className == this.javaClass.name) {
                hasWrapManager = true
            }
        }
        element ?: throw IllegalStateException("Can't find the stack element to access")
        val clazz = Class.forName(element.className)
        val method = clazz.getMethodByName(element.methodName)
        val targetClass = AccessUtils.getObfClassByName(clazz.getAnnotation(Wrapper::class.java)?.name
            ?: throw IllegalAccessException("Unable to find annotation for class"))
        method.getAnnotation(Wrapper::class.java)?.also {
            when(it.type) {
                EnumWrapType.FIELD -> {
                    val field = AccessUtils.getObfFieldByName(targetClass, it.name)
                    field.isAccessible = true
                    return if(args.isEmpty()) {
                        field.get(instance)
                    } else {
                        field.set(instance, args[0])
                    }
                }
                EnumWrapType.METHOD -> {
                    val info = it.name.split("!")
                    val targetMethod = AccessUtils.getObfMethodByName(targetClass, info[0], info[1])
                    targetMethod.isAccessible = true
                    return targetMethod.invoke(instance, *args)
                }
                EnumWrapType.CLASS -> throw IllegalArgumentException("Class type is not allowed on method")
            }
        }
        throw IllegalAccessException("Unable to find annotation for method")
    }

    fun getter(instance: Any?, className: String, name: String)
            = AccessUtils.getObfFieldByName(AccessUtils.getObfClassByName(className), name).also { it.isAccessible = true }.get(instance)

    fun setter(instance: Any?, className: String, name: String, value: Any?)
            = AccessUtils.getObfFieldByName(AccessUtils.getObfClassByName(className), name).also { it.isAccessible = true }.set(instance, value)

    fun call(instance: Any?, className: String, name: String, desc: String, vararg args: Any?)
            = AccessUtils.getObfMethodByName(AccessUtils.getObfClassByName(className), name, desc).also { it.isAccessible = true }.invoke(instance, *args)

    fun origin(className: String)
            = AccessUtils.getObfClassByName(className)
}