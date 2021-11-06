package me.liuli.luminous.utils.extension

import javassist.ClassPool
import javassist.CtClass
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
    = AccessUtils.getObfFieldByName(this, name)

fun Class<*>.getObfMethod(name: String, signature: String)
    = AccessUtils.getObfMethodByName(this, name, signature)

fun Class<*>.getObfFieldAccessable(name: String)
    = AccessUtils.getObfFieldByName(this, name).also { it.isAccessible = true }

fun Class<*>.getObfMethodAccessable(name: String, signature: String)
    = AccessUtils.getObfMethodByName(this, name, signature).also { it.isAccessible = true }

fun Class<*>.getObfFieldValue(name: String, instance: Any? = null)
    = getObfFieldAccessable(name).get(instance)

fun Class<*>.setObfFieldValue(name: String, instance: Any? = null, value: Any)
    = getObfFieldAccessable(name).set(instance, value)

fun Class<*>.invokeObfMethod(name: String, signature: String, instance: Any? = null, vararg args: Any)
    = getObfMethodAccessable(name, signature).invoke(instance, *args)

// https://stackoverflow.com/questions/45072268/how-can-i-get-the-signature-field-of-java-reflection-method-object
val Method.signature: String
    get() {
        try {
            val field = Method::class.java.getDeclaredField("signature")
            field.isAccessible = true
            val signature = field.get(this) as String?
            if(signature!=null) return signature
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

        return sb.toString()
    }

val Class<*>.ctClass: CtClass
    get() = ClassPool.getDefault().getCtClass(this.name)