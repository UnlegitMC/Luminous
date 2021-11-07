package me.liuli.luminous.event

open class Event

open class EventCancellable : Event() {
    /**
     * determines whether the event is cancelled
     */
    var cancel = false
}