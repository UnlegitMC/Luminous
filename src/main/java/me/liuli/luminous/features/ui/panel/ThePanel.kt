package me.liuli.luminous.features.ui.panel

import me.liuli.luminous.Luminous
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.event.Render2DEvent
import me.liuli.luminous.features.ui.HudManager
import me.liuli.luminous.utils.extension.get
import me.liuli.luminous.utils.extension.invoke
import me.liuli.luminous.utils.game.mc
import me.liuli.luminous.utils.jvm.AccessUtils
import me.liuli.luminous.utils.misc.logInfo
import me.liuli.luminous.utils.render.RenderUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color

object ThePanel {
    val width = 200f
    val height = 250f

    var x = 0f
    var y = 0f
    var inFocus = false

    var inDrag = false
    var dragX = 0f
    var dragY = 0f

    var mouseX = 0f
        private set
    var mouseY = 0f
        private set

    private val pages = AccessUtils.resolvePackage("me.liuli.luminous.features.ui.panel.pages", Page::class.java)
        .filter { !it.isAssignableFrom(SubPage::class.java) }
        .map { AccessUtils.getInstance(it) }.sortedBy { it.name }.toMutableList()
    private var nowPage = pages[0]

    fun onRender(posX: Int, posY: Int, pTicks: Float) {
        val fontRenderer = mc.fontRendererObj!!
        val fontHeight = fontRenderer.FONT_HEIGHT!!.toFloat()
        val mouseX = posX - x
        val mouseY = posY - y
        this.mouseX = mouseX
        this.mouseY = mouseY

        // handle mouse drag
        if(inDrag) {
            if(Mouse.isButtonDown(0)) {
                x = posX - dragX
                y = posY - dragY
            } else {
                inDrag = false
            }
        }

        // make sure the panel is in the screen
        if(x < 0) x = 0f
        if(y < 0) y = 0f
        if(x + width > HudManager.scaledWidth) x = HudManager.scaledWidth - width
        if(y + height > HudManager.scaledHeight) y = HudManager.scaledHeight - height

        GL11.glPushMatrix()
        GL11.glTranslatef(x, y, 0f)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)

        RenderUtils.glColor(if(inFocus) { Color.LIGHT_GRAY } else { Color.DARK_GRAY })
        RenderUtils.quickDrawRect(0f, 0f, width, fontHeight + 4)
        RenderUtils.glColor(Color.BLACK)
        RenderUtils.quickDrawRect(0f, fontHeight + 4, width, height)

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)

        val pageIndex = pages.indexOf(nowPage)

        fontRenderer.drawString(if(nowPage is SubPage) { "◄ ${(nowPage as SubPage).parent.name}" }
            else { "${Luminous.NAME} - ${if(pageIndex>0) { "▲ " } else {""}}${nowPage.name}${if(pageIndex<(pages.size-1)) { " ▼" } else {""}}" }, 2f, 2f, 0xFFFFFF, false)

        GL11.glTranslatef(0f, fontHeight + 4, 0f)
        nowPage.onRender(mouseX, mouseY - 10, pTicks)

        GL11.glPopMatrix()
    }

    fun handleMouseInput(returnInfo: HookReturnInfo) {
        val wheel = Mouse.getEventDWheel()
        val pageIndex = pages.indexOf(nowPage)

        if(wheel != 0 && inFocus) {
            if(RenderUtils.posInArea(mouseX, mouseY, 0f, 0f, width, mc.fontRendererObj!!.FONT_HEIGHT!!.toFloat() + 4f)) {
                nowPage = pages[(pageIndex + if(wheel>0) { -1 } else { 1 }).let {
                    if(it<0 || it>=pages.size) { pageIndex } else { it }
                }]
            } else {
                nowPage.onMouseWheel(wheel)
            }
            returnInfo.`return`()
        }
    }

    fun keyTyped(returnInfo: HookReturnInfo, key: Char, keyCode: Int) {
        if(inFocus && keyCode != 1) {
            nowPage.onKeyType(key, keyCode)
            returnInfo.`return`()
        }
    }

    fun mouseClicked(posX: Int, posY: Int, button: Int) {
        inFocus = RenderUtils.posInArea(posX.toFloat(), posY.toFloat(), x, y, x + width, y + height)
        if(!inFocus) return
        val mouseX = posX - x
        val mouseY = posY - y

        if(button == 0 && RenderUtils.posInArea(mouseX, mouseY, 0f, 0f, width, mc.fontRendererObj!!.FONT_HEIGHT!!.toFloat() + 4f)) {
            inDrag = true
            dragX = mouseX
            dragY = mouseY
        }

        nowPage.onMouseClick(mouseX, mouseY, button)
    }
}