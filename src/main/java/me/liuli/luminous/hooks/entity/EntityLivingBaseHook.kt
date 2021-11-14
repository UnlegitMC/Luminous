package me.liuli.luminous.hooks.entity

import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.event.EventManager
import me.liuli.luminous.event.JumpEvent
import me.liuli.luminous.utils.jvm.AccessUtils
import wrapped.net.minecraft.entity.EntityLivingBase
import wrapped.net.minecraft.potion.Potion
import kotlin.math.cos
import kotlin.math.sin

class EntityLivingBaseHook : HookFunction(AccessUtils.getObfClassByName("net.minecraft.entity.EntityLivingBase")) {
    @Hook(target = "jump!()V", type = HookType.METHOD_ENTER, returnable = true)
    fun jump(returnInfo: HookReturnInfo) {
        val event = JumpEvent()
        EventManager.callEvent(event)
        returnInfo.cancel = event.cancel
    }
}