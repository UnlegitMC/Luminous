package me.liuli.luminous.utils.misc

import com.beust.klaxon.JsonArray

object JsonUtils {
    fun <T> toJsonArray(array: Array<T>): JsonArray<T> {
        val json = JsonArray<T>()

        array.forEach {
            json.add(it)
        }

        return json
    }
}