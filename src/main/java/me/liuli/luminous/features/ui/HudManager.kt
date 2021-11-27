package me.liuli.luminous.features.ui

import me.liuli.luminous.event.*
import me.liuli.luminous.features.ui.panel.ThePanel
import me.liuli.luminous.utils.jvm.AccessUtils
import me.liuli.luminous.utils.misc.logError
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

    init {
        EventManager.registerListener(this)
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
    }

    @EventHandler
    fun onUpdate(event: UpdateEvent) {
        elements.forEach { it.update() }
    }

    override val listen: Boolean
        get() = true
}