package me.liuli.luminous.features.value

/**
 * Float value represents a value with a float
 */
open class FloatValue(name: String, value: Float, val minimum: Float = 0F, val maximum: Float = Float.MAX_VALUE) : Value<Float>(name, value) {

    fun set(newValue: Number) {
        set(newValue.toFloat())
    }

    override fun serialize() = value

    override fun deserialize(element: Any) {
        if (element is Number) {
            value = element.toFloat()
        }
    }
}