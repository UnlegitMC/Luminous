package me.liuli.luminous.utils.extension

import me.liuli.luminous.utils.jvm.AccessUtils
import java.lang.reflect.Method

fun Class<*>.getMethodsByName(name: String)
    = this.declaredMethods.filter { it.name == name }

fun Class<*>.getMethodByName(name: String)
        = this.getMethodsByName(name).firstOrNull() ?: throw NoSuchMethodException(name)

fun Class<*>.getFieldsByName(name: String)
        = this.declaredFields.filter { it.name == name }

fun Class<*>.getFieldByName(name: String)
        = this.getFieldsByName(name).firstOrNull() ?: throw NoSuchFieldException(name)

fun Class<*>.getObfField(name: String)
    = AccessUtils.getObfField(this, name)

fun Class<*>.getObfMethod(name: String, signature: String)
    = AccessUtils.getObfMethod(this, name, signature)

fun Class<*>.getObfFieldValue(name: String, instance: Any? = null)
    = getObfField(name).get(instance)

fun Class<*>.setObfFieldValue(name: String, value: Any, instance: Any? = null)
    = getObfField(name).set(instance, value)

fun Class<*>.invokeObfMethod(name: String, signature: String, vararg args: Any, instance: Any? = null)
    = getObfMethod(name, signature).invoke(instance, *args)

// https://stackoverflow.com/questions/45072268/how-can-i-get-the-signature-field-of-java-reflection-method-object
val Method.signature: String
    get() {
        try {
            val field = Method::class.java.getDeclaredField("signature")
            field.isAccessible = true
            val signature = field.get(this) as String?
            if (signature != null) return signature
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }

        val sb = StringBuilder("(")
        for (c in this.parameterTypes) {
            java.lang.reflect.Array.newInstance(c, 0).toString().let {
                sb.append(it.substring(1, it.indexOf('@')))
            }
        }
        sb.append(')')
            .append(if (this.returnType === Void.TYPE) { "V" } else { java.lang.reflect.Array.newInstance(this.returnType, 0).toString().let { it.substring(1, it.indexOf('@')) } })

        return sb.toString().replace(".", "/")
    }