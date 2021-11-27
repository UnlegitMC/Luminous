package me.liuli.luminous.features.ui.panel

abstract class Page(val name: String) {
    open fun onRender(mouseX: Float, mouseY: Float, pTicks: Float) {}

    open fun onMouseClick(mouseX: Float, mouseY: Float, button: Int) {}

    open fun onMouseWheel(delta: Int) {}

    open fun onKeyType(typedChar: Char, keyCode: Int) {}
}