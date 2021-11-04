package me.liuli.luminous.utils.extension

fun Class<*>.getMethodsByName(name: String)
    = this.declaredMethods.filter { it.name == name }

fun Class<*>.getMethodByName(name: String)
        = this.getMethodsByName(name).firstOrNull() ?: throw NoSuchMethodException(name)

fun Class<*>.getFieldsByName(name: String)
        = this.declaredFields.filter { it.name == name }

fun Class<*>.getFieldByName(name: String)
        = this.getFieldsByName(name).firstOrNull() ?: throw NoSuchFieldException(name)