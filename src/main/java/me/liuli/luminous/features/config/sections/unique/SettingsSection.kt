package me.liuli.luminous.features.config.sections.unique

import com.beust.klaxon.JsonObject
import me.liuli.luminous.features.config.ConfigManager
import me.liuli.luminous.features.config.ConfigSection

class SettingsSection : ConfigSection("settings", true) {
    override fun load(json: JsonObject) {
        ConfigManager.switchConfig(json.getOrDefault("config-name", "your_config").toString(), forInitialize = true)
    }

    override fun save(json: JsonObject) {
        json["config-name"] = ConfigManager.configName
    }
}