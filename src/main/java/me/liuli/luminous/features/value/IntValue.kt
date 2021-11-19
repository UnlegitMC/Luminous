package me.liuli.luminous.features.value

/**
 * Integer value represents a value with a integer
 */
open class IntValue(name: String, value: Int, val minimum: Int = 0, val maximum: Int = Integer.MAX_VALUE) : Value<Int>(name, value) {

    fun set(newValue: Number) {
        set(newValue.toInt())
    }

    override fun serialize() = value

    override fun deserialize(element: Any) {
        if (element is Number) {
            value = element.toInt()
        }
    }
}