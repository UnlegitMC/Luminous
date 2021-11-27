package me.liuli.luminous.features.ui.panel.pages

import me.liuli.luminous.features.ui.panel.Page
import me.liuli.luminous.utils.game.mc

class ModulesPage : Page("Modules") {
    override fun onRender(mouseX: Float, mouseY: Float, pTicks: Float) {
        mc.fontRendererObj!!.drawString("TEST", 10, 10, 0xFFFFFF)
    }
}