package me.liuli.luminous.event

interface Listener {
    /**
     * determines whether the listener is enabled
     */
    val listen: Boolean
}