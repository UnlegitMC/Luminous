package me.liuli.luminous.hooks.entity

import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.event.EventManager
import me.liuli.luminous.event.StrafeEvent
import me.liuli.luminous.utils.jvm.AccessUtils

class EntityHook : HookFunction(AccessUtils.getObfClassByName("net.minecraft.entity.Entity")) {
    @Hook(target = "moveFlying!(FFF)V", type = HookType.METHOD_ENTER, getInstance = true, returnable = true, getParameters = true)
    fun moveFlying(instance: Any, returnInfo: HookReturnInfo, strafe: Float, forward: Float, friction: Float) {
        StrafeEvent(strafe, forward, friction).also {
            EventManager.callEvent(it)
            if(it.cancel) returnInfo.cancel = true
        }
    }
}