package me.liuli.luminous.features.value

/**
 * List value represents a selectable list of values
 */
open class ListValue(name: String, val values: Array<String>, value: String) : Value<String>(name, value) {
    init {
        this.value = value
    }

    operator fun contains(value: String): Boolean {
        return values.any { it.equals(value, true) }
    }

    override fun changeValue(value: String) {
        this.value = values.find { it.equals(value, true) } ?: return
    }

    override fun serialize() = value

    override fun deserialize(element: Any) {
        if(element is String) {
            changeValue(element)
        }
    }
}