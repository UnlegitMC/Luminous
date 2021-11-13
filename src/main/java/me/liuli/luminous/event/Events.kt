package me.liuli.luminous.event

import wrapped.net.minecraft.client.multiplayer.WorldClient
import wrapped.net.minecraft.entity.Entity

class PreUpdateEvent : Event()

class PostUpdateEvent : Event()

class PreMotionEvent : Event()

class PostMotionEvent : Event()

class JumpEvent : EventCancellable()

class TickEvent : Event()

class WorldEvent(val world: WorldClient?) : Event()

class KeyEvent(val key: Int) : Event()

class PushOutEvent : EventCancellable()

class AttackEvent(val targetEntity: Entity) : EventCancellable()

class Render2DEvent(val partialTicks: Float) : Event()

class Render3DEvent(val partialTicks: Float) : Event()

// TODO: PacketEvent, SlowDownEvent