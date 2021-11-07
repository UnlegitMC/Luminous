package me.liuli.luminous.agent.hook.reflect

import me.liuli.luminous.utils.extension.getObfField
import me.liuli.luminous.utils.extension.getObfMethodAccessable

/**
 * a wrapper for [Class] to make method and field access easier
 * @author liulihaocai
 */
open class WrappedClass(val clazz: Class<*>) {
    /**
     * call a static method
     * @param name of the method
     * @param sign method signature
     * @param args method arguments
     */
    fun invokeStatic(name: String, sign: String, vararg args: Any?): Any? {
        return clazz.getObfMethodAccessable(name, sign).invoke(null, *args)
    }

    /**
     * call a dynamic method
     * @param name of the method
     * @param sign method signature
     * @param instance of this class
     * @param args method arguments
     */
    fun invokeDynamic(name: String, sign: String, instance: Any, vararg args: Any?): Any? {
        return clazz.getObfMethodAccessable(name, sign).invoke(instance, *args)
    }

    /**
     * get a static field
     * @param name of the field
     */
    fun getStatic(name: String) = clazz.getObfField(name).get(null)

    /**
     * set a static field
     * @param name of the field
     * @param value that you wanna set to
     */
    fun setStatic(name: String, value: Any) = clazz.getObfField(name).set(null, value)

    /**
     * get a dynamic field
     * @param name of the field
     * @param instance of this class
     */
    fun getDynamic(name: String, instance: Any) = clazz.getObfField(name).get(instance)

    /**
     * set a dynamic field
     * @param name of the field
     * @param instance of this class
     * @param value that you wanna set to
     */
    fun setDynamic(name: String, instance: Any, value: Any) = clazz.getObfField(name).set(instance, value)
}