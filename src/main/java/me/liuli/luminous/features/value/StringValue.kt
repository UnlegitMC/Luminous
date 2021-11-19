package me.liuli.luminous.features.value

/**
 * Text value represents a value with a string
 */
open class StringValue(name: String, value: String) : Value<String>(name, value) {
    override fun serialize() = value

    override fun deserialize(element: Any) {
        if (element is String) {
            value = element
        }
    }
}