package me.liuli.luminous.event

class PreUpdateEvent : Event()

class PostUpdateEvent : Event()

class PreMotionEvent : Event()

class PostMotionEvent : Event()

class TickEvent : Event()

class WorldEvent(val world: Any?) : Event()

class KeyEvent(val key: Int) : Event()

class PushOutEvent : EventCancellable()

class AttackEvent(val targetEntity: Any) : Event()

class Render2DEvent(val partialTicks: Float) : Event()

class Render3DEvent(val partialTicks: Float) : Event()

// TODO: PacketEvent, SlowDownEvent