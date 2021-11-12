package me.liuli.luminous.utils.extension

import me.liuli.luminous.utils.jvm.AccessUtils

fun Any.invoke(method: String, sign: String, vararg args: Any)
    = AccessUtils.getObfMethodByName(this.javaClass, method, sign).also { it.isAccessible = true }.invoke(this, *args)

fun Any.get(field: String)
        = AccessUtils.getObfFieldByName(this.javaClass, field).also { it.isAccessible = true }.get(this)