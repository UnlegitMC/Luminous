package me.liuli.luminous.event

import java.lang.reflect.Method

/**
 * cache for [Method] and [Listener]
 */
class ListenerMethod(val method: Method, val listener: Listener) {
    val listen: Boolean
        get() = listener.listen

    val priority: Int
        get() = method.getAnnotation(EventHandler::class.java).priority

    init {
        method.isAccessible = true
    }

    fun invoke(event: Event) {
        method.invoke(listener, event)
    }
}