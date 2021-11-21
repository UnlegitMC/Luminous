package me.liuli.luminous.event

import me.liuli.luminous.utils.misc.logError

object EventManager {
    private val listeners = mutableMapOf<Class<*>, MutableList<ListenerMethod>>()

    /**
     * register a listener to the event manager.
     */
    fun registerListener(listener: Listener) {
        for (method in listener.javaClass.declaredMethods) {
            if (method.isAnnotationPresent(EventHandler::class.java)) {
                (listeners[method.parameterTypes[0]] ?: mutableListOf<ListenerMethod>().also {
                    listeners[method.parameterTypes[0]] = it
                }).add(ListenerMethod(method, listener))
            }
        }
    }

    /**
     * call [event] listeners
     */
    fun callEvent(event: Event) {
        listeners[event.javaClass]?.sortedBy { it.priority }?.forEach {
            if(it.listener.listen) {
                try {
                    it.method.invoke(it.listener, event)
                } catch (t: Throwable) {
                    logError("An error occurred while handling the event: $t")
                }
            }
        }
    }
}