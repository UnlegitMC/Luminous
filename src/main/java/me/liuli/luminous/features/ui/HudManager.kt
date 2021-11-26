package me.liuli.luminous.features.ui

import me.liuli.luminous.event.*
import me.liuli.luminous.features.ui.panel.ThePanel
import me.liuli.luminous.utils.game.mc
import me.liuli.luminous.utils.jvm.AccessUtils
import me.liuli.luminous.utils.misc.logError
import me.liuli.luminous.utils.misc.logInfo
import org.lwjgl.opengl.GL11

object HudManager: Listener {
    val elements = mutableListOf<HudElement>()
    val elementTypes = AccessUtils.resolvePackage("me.liuli.luminous.features.ui.hud.elements", HudElement::class.java).toMutableList()

    var scaledWidth = 0
        private set
    var scaledHeight = 0
        private set
    var scaleFactor = 0
        private set

    // for panel
    private val guiChatClass = AccessUtils.getObfClass("net.minecraft.client.gui.GuiChat")

    init {
        EventManager.registerListener(this)
        ThePanel
    }

    @EventHandler
    fun onRender2d(event: Render2DEvent) {
        scaledWidth = event.sr.scaledWidth!!
        scaledHeight = event.sr.scaledHeight!!
        scaleFactor = event.sr.scaleFactor!!

        elements.forEach { element ->
            GL11.glPushMatrix()
            GL11.glScalef(element.scale, element.scale, element.scale)
            GL11.glTranslatef(element.renderX, element.renderY, 0f)

            try {
                element.render(event)
            } catch (e: Exception) {
                logError("Something went wrong while drawing ${element.name} element in HUD($e)")
            }

            GL11.glPopMatrix()
        }
        if(guiChatClass.isInstance(mc.currentScreen?.theInstance ?: this)) {
            ThePanel.onRender(event)
        }
    }

    @EventHandler
    fun onUpdate(event: UpdateEvent) {
        elements.forEach { it.update() }
    }

    override val listen: Boolean
        get() = true
}