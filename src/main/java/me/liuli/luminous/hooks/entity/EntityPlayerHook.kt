package me.liuli.luminous.hooks.entity

import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.event.EventManager
import me.liuli.luminous.event.SlowDownEvent
import me.liuli.luminous.utils.jvm.AccessUtils
import wrapped.net.minecraft.client.entity.EntityPlayerSP

class EntityPlayerHook : HookFunction(AccessUtils.getObfClass("net.minecraft.entity.player.EntityPlayer")) {

    @Hook(target = "isUsingItem!()Z", type = HookType.METHOD_ENTER, getInstance = true, returnable = true)
    fun onUpdateWalkingPlayerEnter(instance_: Any, returnInfo: HookReturnInfo) {
        if(EntityPlayerSPHook.flag) {
            val event = SlowDownEvent(0.2f, 0.2f)
            val instance = EntityPlayerSP(instance_)
            EventManager.callEvent(event)
            instance.movementInput!!.moveStrafe = instance.movementInput!!.moveStrafe!! * event.strafe
            instance.movementInput!!.moveForward = instance.movementInput!!.moveForward!! * event.forward

            returnInfo.cancel = true
            returnInfo.returnValue = false
            EntityPlayerSPHook.flag = false
        }
    }
}