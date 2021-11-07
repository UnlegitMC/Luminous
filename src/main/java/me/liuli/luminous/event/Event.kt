package me.liuli.luminous.event

open class Event

open class EventCancellable : Event() {
    var cancel = false
}