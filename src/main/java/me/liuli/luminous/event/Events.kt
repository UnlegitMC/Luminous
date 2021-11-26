package me.liuli.luminous.event

import wrapped.net.minecraft.client.gui.ScaledResolution
import wrapped.net.minecraft.client.multiplayer.WorldClient
import wrapped.net.minecraft.entity.Entity

class UpdateEvent : Event()

class PreMotionEvent : Event()

class PostMotionEvent : Event()

class JumpEvent : EventCancellable()

class TickEvent : Event()

class WorldEvent(val world: WorldClient?) : Event()

class KeyEvent(val key: Int) : Event()

class PushOutEvent : EventCancellable()

class AttackEvent(val targetEntity: Entity) : EventCancellable()

class Render2DEvent(val sr: ScaledResolution, val partialTicks: Float) : Event()

class Render3DEvent(val partialTicks: Float) : Event()

class StrafeEvent(val strafe: Float, val forward: Float, val friction: Float) : EventCancellable()

//class BlockBBEvent(blockPos: BlockPos, val block: Block, var boundingBox: AxisAlignedBB?) : Event() {
//    val x = blockPos.x
//    val y = blockPos.y
//    val z = blockPos.z
//}

//class ClickBlockEvent(val clickedBlock: BlockPos?, val enumFacing: EnumFacing?) : Event()

class SlowDownEvent(var strafe: Float, var forward: Float) : Event()

//class PacketEvent(val packet: Packet<*>, val type: Type) : EventCancellable() {
//    enum class Type {
//        RECEIVE,
//        SEND
//    }
//
//    fun isServerSide() = type == Type.RECEIVE
//}

class MoveEvent(var x: Double, var y: Double, var z: Double) : EventCancellable() {
    fun zero() {
        x = 0.0
        y = 0.0
        z = 0.0
    }

    fun zeroXZ() {
        x = 0.0
        z = 0.0
    }
}

// TODO: PacketEvent, SlowDownEvent