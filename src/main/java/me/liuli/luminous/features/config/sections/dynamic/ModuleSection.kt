package me.liuli.luminous.features.config.sections.dynamic

import com.beust.klaxon.JsonObject
import me.liuli.luminous.features.config.ConfigSection
import me.liuli.luminous.features.module.ModuleManager
import me.liuli.luminous.features.module.ModuleTriggerType

class ModuleSection : ConfigSection("module", false) {
    override fun load(json: JsonObject) {
        ModuleManager.modules.forEach { module ->
            val config = json[module.name] as JsonObject? ?: JsonObject()
            module.state = config["state"] as Boolean? ?: module.defaultOn
            module.keyBind = config["bind"] as Int? ?: module.defaultKeyBind
            module.triggerType = ModuleTriggerType.valueOf(config["trigger"] as String? ?: module.triggerType.toString())
            val valueConfig = config["values"] as JsonObject? ?: JsonObject()
            module.values.forEach { value ->
                val valueData = valueConfig[value.name]
                if(valueData != null) {
                    value.deserialize(valueData)
                } else {
                    value.reset()
                }
            }
        }
    }

    override fun save(json: JsonObject) {
        ModuleManager.modules.forEach { module ->
            val config = JsonObject()
            config["state"] = module.state
            config["bind"] = module.keyBind
            config["trigger"] = module.triggerType.toString()
            val valueConfig = JsonObject()
            module.values.forEach { value ->
                config[value.name] = value.serialize()
            }
            config["values"] = valueConfig
            json[module.name] = config
        }
    }
}