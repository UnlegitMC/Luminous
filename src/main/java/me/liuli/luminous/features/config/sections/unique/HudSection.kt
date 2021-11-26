package me.liuli.luminous.features.config.sections.unique

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import me.liuli.luminous.features.config.ConfigSection
import me.liuli.luminous.features.ui.ElementSide
import me.liuli.luminous.features.ui.HudManager
import me.liuli.luminous.utils.jvm.AccessUtils
import me.liuli.luminous.utils.misc.JsonUtils

class HudSection : ConfigSection("hud", true) {
    override fun load(json: JsonObject) {
        HudManager.elements.clear()
        val elementsArr = json["elements"] as JsonArray<JsonObject>? ?: return
        for(obj in elementsArr) {
            val clazz = HudManager.elementTypes.find { it.canonicalName == obj["type"] as String? } ?: continue
            val element = AccessUtils.getInstanceOrNull(clazz) ?: continue
            element.x = (obj["x"] as Number).toFloat()
            element.y = (obj["y"] as Number).toFloat()
            element.scale = (obj["scale"] as Number).toFloat()
            element.side = ElementSide.default().also { it.deserialize(obj["side"] as JsonObject) }
            JsonUtils.deserializeValues(obj["values"] as JsonObject, element.values)
            HudManager.elements.add(element)
        }
    }

    override fun save(json: JsonObject) {
       val elementsArr = JsonArray<JsonObject>()
        HudManager.elements.forEach { element ->
            val obj = JsonObject()
            obj["type"] = element.javaClass.canonicalName
            obj["x"] = element.x
            obj["y"] = element.y
            obj["scale"] = element.scale
            obj["side"] = element.side.serialize()
            obj["values"] = JsonUtils.serializeValues(element.values)
            elementsArr.add(obj)
        }
        json["elements"] = elementsArr
    }
}