package me.liuli.luminous.wrapper

import me.liuli.luminous.utils.jvm.AccessUtils

object WrapManager {
    fun getter(instance: Any?, className: String, name: String)
            = AccessUtils.getObfField(AccessUtils.getObfClass(className), name).get(instance)

    fun setter(instance: Any?, className: String, name: String, value: Any?)
            = AccessUtils.getObfField(AccessUtils.getObfClass(className), name).set(instance, value)

    fun call(instance: Any?, className: String, name: String, desc: String, vararg args: Any?)
            = AccessUtils.getObfMethod(AccessUtils.getObfClass(className), name, desc).invoke(instance, *args)

    fun origin(className: String)
            = AccessUtils.getObfClass(className)
}