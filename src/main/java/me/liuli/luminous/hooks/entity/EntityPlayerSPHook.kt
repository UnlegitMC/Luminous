package me.liuli.luminous.hooks.entity

import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.event.*
import me.liuli.luminous.utils.jvm.AccessUtils
import wrapped.net.minecraft.client.entity.EntityPlayerSP

class EntityPlayerSPHook : HookFunction(AccessUtils.getObfClass("net.minecraft.client.entity.EntityPlayerSP")) {
    companion object {
        var flag = false
    }

    @Hook(target = "onUpdateWalkingPlayer!()V", type = HookType.METHOD_ENTER)
    fun onUpdateWalkingPlayerEnter() {
        EventManager.callEvent(PreMotionEvent())
    }

    @Hook(target = "onUpdateWalkingPlayer!()V", type = HookType.METHOD_EXIT)
    fun onUpdateWalkingPlayerExit() {
        EventManager.callEvent(PostMotionEvent())
    }

    @Hook(target = "pushOutOfBlocks!(DDD)Z", type = HookType.METHOD_ENTER, getInstance = true, returnable = true)
    fun pushOutOfBlocks(instance_: Any, returnInfo: HookReturnInfo) {
        val instance = EntityPlayerSP(instance_)
        val event = PushOutEvent()
        event.cancel = instance.noClip!!
        EventManager.callEvent(event)
        if(event.cancel) {
            returnInfo.cancel = true
            returnInfo.returnValue = false
        }
    }

    @Hook(target = "onLivingUpdate!()V", type = HookType.METHOD_ENTER, getInstance = true)
    fun onLivingUpdateEnter(instance_: Any) {
        val instance = EntityPlayerSP(instance_)
        EventManager.callEvent(UpdateEvent())
        flag = instance.isUsingItem()!! && instance.isRiding()!!
    }
}