package me.liuli.luminous.utils.misc

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import me.liuli.luminous.features.value.Value

object JsonUtils {
    fun <T> toJsonArray(array: Array<T>): JsonArray<T> {
        val json = JsonArray<T>()

        array.forEach {
            json.add(it)
        }

        return json
    }

    fun serializeValues(values: List<Value<*>>): JsonObject {
        val json = JsonObject()
        values.forEach { value ->
            json[value.name] = value.serialize()
        }
        return json
    }

    fun deserializeValues(json: JsonObject, values: List<Value<*>>) {
        values.forEach { value ->
            val valueData = json[value.name]
            if(valueData != null) {
                value.deserialize(valueData)
            } else {
                value.reset()
            }
        }
    }
}