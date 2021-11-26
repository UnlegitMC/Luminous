package me.liuli.luminous.features.ui

import me.liuli.luminous.event.Render2DEvent
import me.liuli.luminous.features.value.Value
import me.liuli.luminous.utils.jvm.AccessUtils

abstract class HudElement(val name: String, var x: Float = 0f, var y: Float = 0f, var scale: Float = 1f, var side: ElementSide = ElementSide.default()) {
    var renderX: Float
        get() = when (side.horizontal) {
            ElementSide.Horizontal.LEFT -> x
            ElementSide.Horizontal.MIDDLE -> (HudManager.scaledWidth / 2) - x
            ElementSide.Horizontal.RIGHT -> HudManager.scaledWidth - x
        }
        set(value) = when (side.horizontal) {
            ElementSide.Horizontal.LEFT -> {
                x += value
            }
            ElementSide.Horizontal.MIDDLE, ElementSide.Horizontal.RIGHT -> {
                x -= value
            }
        }

    var renderY: Float
        get() = when (side.vertical) {
            ElementSide.Vertical.UP -> y
            ElementSide.Vertical.MIDDLE -> (HudManager.scaledHeight / 2) - y
            ElementSide.Vertical.DOWN -> HudManager.scaledHeight - y
        }
        set(value) = when (side.vertical) {
            ElementSide.Vertical.UP -> {
                y += value
            }
            ElementSide.Vertical.MIDDLE, ElementSide.Vertical.DOWN -> {
                y -= value
            }
        }

    /**
     * called every [me.liuli.luminous.event.Render2DEvent]
     */
    abstract fun render(event: Render2DEvent)

    /**
     * called every [me.liuli.luminous.event.UpdateEvent]
     */
    open fun update() {}

    val values: List<Value<*>>
        get() = AccessUtils.getValues(this)

    /**
     * get a value but with kotlin feature
     */
    operator fun get(valueName: String) = this.values.find { it.name.equals(valueName, ignoreCase = true) }
}