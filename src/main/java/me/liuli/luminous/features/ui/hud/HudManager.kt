package me.liuli.luminous.features.ui.hud

import me.liuli.luminous.event.*
import me.liuli.luminous.utils.game.mc
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
        elements.forEach { element ->
            GL11.glPushMatrix()
            GL11.glScalef(element.scale, element.scale, element.scale)
            GL11.glTranslatef(element.renderX, element.renderY, 0f)

            try {
                element.render()
            } catch (e: Exception) {
                logError("Something went wrong while drawing ${element.name} element in HUD($e)")
            }

            GL11.glPopMatrix()
        }
    }

    @EventHandler
    fun onUpdate(event: UpdateEvent) {
        scaledWidth = mc.displayWidth!!
        scaledHeight = mc.displayHeight!!
        scaleFactor = 1
        val flag = mc.isUnicode()!!
        var i = mc.gameSettings!!.guiScale!!
        if (i == 0) {
            i = 1000
        }
        while (scaleFactor < i && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor
        }
        if (flag && scaleFactor % 2 != 0 && scaleFactor != 1) {
            --scaleFactor
        }
        scaledWidth = (scaledWidth.toDouble() / scaleFactor.toDouble()).toInt()
        scaledHeight = (scaledHeight.toDouble() / scaleFactor.toDouble()).toInt()

        elements.forEach { it.update() }
    }

    override val listen: Boolean
        get() = true
}