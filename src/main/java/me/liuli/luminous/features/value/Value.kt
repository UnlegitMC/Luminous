package me.liuli.luminous.features.value

import me.liuli.luminous.utils.misc.logWarn

abstract class Value<T>(val name: String, valueIn: T) {
    var value = valueIn
        protected set
    val defaultValue = value

    private var displayableFunc: () -> Boolean = { true }

    fun displayable(func: () -> Boolean): Value<T> {
        displayableFunc = func
        return this
    }

    val displayable: Boolean
        get() = displayableFunc()

    fun set(newValue: T) {
        if (newValue == value) return

        val oldValue = value

        try {
            onChange(oldValue, newValue)
            changeValue(newValue)
            onChanged(oldValue, newValue)
        } catch (e: Exception) {
            logWarn("[ValueSystem ($name)]: ${e.javaClass.name} (${e.message}) [$oldValue >> $newValue]")
        }
    }

    open fun changeValue(value: T) {
        this.value = value
    }

    open fun reset() {
        value = defaultValue
    }

    abstract fun serialize(): Any
    abstract fun deserialize(element: Any)

    protected open fun onChange(oldValue: T, newValue: T) {}
    protected open fun onChanged(oldValue: T, newValue: T) {}
}