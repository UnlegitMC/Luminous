package me.liuli.luminous.hooks.entity

import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.event.EventManager
import me.liuli.luminous.event.MoveEvent
import me.liuli.luminous.event.StrafeEvent
import me.liuli.luminous.utils.jvm.AccessUtils
import wrapped.net.minecraft.client.entity.EntityPlayerSP

class EntityHook : HookFunction(AccessUtils.getObfClass("net.minecraft.entity.Entity")) {
    private val entityPlayerSPClass = AccessUtils.getObfClass("net.minecraft.client.entity.EntityPlayerSP")
    private var thisTimeAllow = false

    @Hook(target = "moveFlying!(FFF)V", type = HookType.METHOD_ENTER, getInstance = true, returnable = true, getParameters = true)
    fun moveFlying(instance: Any, returnInfo: HookReturnInfo, strafe: Float, forward: Float, friction: Float) {
        if(!entityPlayerSPClass.isInstance(instance))
            return

        StrafeEvent(strafe, forward, friction).also {
            EventManager.callEvent(it)
            if(it.cancel) returnInfo.cancel = true
        }
    }

    @Hook(target = "moveEntity!(DDD)V", type = HookType.METHOD_ENTER, getInstance = true, returnable = true, getParameters = true)
    fun moveEntity(instance_: Any, returnInfo: HookReturnInfo, x: Double, y: Double, z: Double) {
        if(!entityPlayerSPClass.isInstance(instance_))
            return

        if(thisTimeAllow) {
            thisTimeAllow = false
            return
        }

        val instance = EntityPlayerSP(instance_)
        val event = MoveEvent(x, y, z)
        EventManager.callEvent(event)
        thisTimeAllow = true
        if(!event.cancel) {
            instance.moveEntity(event.x, event.y, event.z)
        }
        returnInfo.cancel = true
    }
}