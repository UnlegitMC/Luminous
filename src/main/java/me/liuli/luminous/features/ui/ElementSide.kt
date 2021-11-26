package me.liuli.luminous.features.ui

import com.beust.klaxon.JsonObject

class ElementSide(var horizontal: Horizontal, var vertical: Vertical) {

    companion object {

        /**
         * Default element side
         */
        fun default() = ElementSide(Horizontal.LEFT, Vertical.UP)
    }

    fun serialize(): JsonObject {
        val json = JsonObject()
        json["horizontal"] = horizontal.name
        json["vertical"] = vertical.name
        return json
    }

    fun deserialize(json: JsonObject) {
        horizontal = Horizontal.getByName(json["horizontal"] as String? ?: "") ?: Horizontal.LEFT
        vertical = Vertical.getByName(json["vertical"] as String? ?: "") ?: Vertical.UP
    }

    /**
     * Horizontal side
     */
    enum class Horizontal(val sideName: String) {

        LEFT("Left"),
        MIDDLE("Middle"),
        RIGHT("Right");

        companion object {

            fun getByName(name: String) = values().find { it.sideName.equals(name, true) }
        }
    }

    /**
     * Vertical side
     */
    enum class Vertical(val sideName: String) {

        UP("Up"),
        MIDDLE("Middle"),
        DOWN("Down");

        companion object {

            fun getByName(name: String) = values().find { it.sideName.equals(name, true) }
        }
    }
}