package me.liuli.luminous.features.config

import com.beust.klaxon.JsonObject

abstract class ConfigSection(val name: String, val isUnique: Boolean) {

    /**
     * the single section of the config from the json file
     */
    abstract fun load(json: JsonObject)

    /**
     * the single section of the config to be written to the file
     */
    abstract fun save(json: JsonObject)
}