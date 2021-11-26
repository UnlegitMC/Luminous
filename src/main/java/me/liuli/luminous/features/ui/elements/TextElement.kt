package me.liuli.luminous.features.ui.elements

import me.liuli.luminous.event.Render2DEvent
import me.liuli.luminous.features.ui.HudElement
import me.liuli.luminous.features.value.BoolValue
import me.liuli.luminous.features.value.IntValue
import me.liuli.luminous.features.value.StringValue
import me.liuli.luminous.utils.game.mc
import java.awt.Color

class TextElement : HudElement("Text") {
    private val displayString = StringValue("DisplayText", "Text")
    private val redValue = IntValue("Red", 255, 0, 255)
    private val greenValue = IntValue("Green", 255, 0, 255)
    private val blueValue = IntValue("Blue", 255, 0, 255)
    private val alphaValue = IntValue("Alpha", 255, 0, 255)
    private val shadow = BoolValue("Shadow", false)

    override fun render(event: Render2DEvent) {
        mc.fontRendererObj!!.drawString(displayString.value, 0f, 0f
            , Color(redValue.value, greenValue.value, blueValue.value, alphaValue.value).rgb, shadow.value)
    }
}