package me.liuli.luminous.features.config.sections.dynamic

import com.beust.klaxon.JsonObject
import me.liuli.luminous.features.config.ConfigSection
import me.liuli.luminous.features.module.ModuleManager
import me.liuli.luminous.features.module.ModuleTriggerType
import me.liuli.luminous.utils.misc.JsonUtils

class ModuleSection : ConfigSection("module", false) {
    override fun load(json: JsonObject) {
        ModuleManager.modules.forEach { module ->
            val config = json[module.name] as JsonObject? ?: JsonObject()
            module.state = config["state"] as Boolean? ?: module.defaultOn
            module.keyBind = config["bind"] as Int? ?: module.defaultKeyBind
            module.triggerType = ModuleTriggerType.valueOf(config["trigger"] as String? ?: module.triggerType.toString())
            JsonUtils.deserializeValues(config["values"] as JsonObject? ?: JsonObject(), module.values)
        }
    }

    override fun save(json: JsonObject) {
        ModuleManager.modules.forEach { module ->
            val config = JsonObject()
            config["state"] = module.state
            config["bind"] = module.keyBind
            config["trigger"] = module.triggerType.toString()
            config["values"] = JsonUtils.serializeValues(module.values)
            json[module.name] = config
        }
    }
}