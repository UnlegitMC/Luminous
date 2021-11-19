package me.liuli.luminous.features.value

/**
 * Bool value represents a value with a boolean
 */
open class BoolValue(name: String, value: Boolean) : Value<Boolean>(name, value) {
    override fun serialize() = value

    override fun deserialize(element: Any) {
        if (element is Boolean) {
            value = element
        } else if (element is String) {
            if(element.lowercase() == "true") {
                value = true
            } else if(element.lowercase() == "false") {
                value = false
            }
        }
    }
}