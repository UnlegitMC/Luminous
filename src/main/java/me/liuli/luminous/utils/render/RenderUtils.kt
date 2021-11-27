package me.liuli.luminous.utils.render

import org.lwjgl.opengl.GL11
import java.awt.Color

object RenderUtils {
    fun glColor(red: Int, green: Int, blue: Int, alpha: Int) {
        GL11.glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255f)
    }

    fun glColor(color: Color) {
        glColor(color, color.alpha / 255f)
    }

    fun glColor(color: Color, alpha: Int) {
        glColor(color, alpha / 255f)
    }

    fun glColor(color: Color, alpha: Float) {
        GL11.glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, alpha)
    }

    fun glColor(hex: Int) {
        GL11.glColor4f((hex shr 16 and 0xFF) / 255f, (hex shr 8 and 0xFF) / 255f, (hex and 0xFF) / 255f, (hex shr 24 and 0xFF) / 255f)
    }

    fun quickDrawRect(x: Float, y: Float, x2: Float, y2: Float) {
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2f(x2, y)
        GL11.glVertex2f(x, y)
        GL11.glVertex2f(x, y2)
        GL11.glVertex2f(x2, y2)
        GL11.glEnd()
    }

    fun drawRect(x: Float, y: Float, x2: Float, y2: Float, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        quickDrawRect(x, y, x2, y2)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    fun posInArea(posX: Float, posY: Float, x: Float, y: Float, x2: Float, y2: Float) : Boolean {
        return posX in x..x2 && posY in y..y2
    }
}