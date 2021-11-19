package me.liuli.luminous.hooks.other

import me.liuli.luminous.agent.hook.impl.Hook
import me.liuli.luminous.agent.hook.impl.HookFunction
import me.liuli.luminous.agent.hook.impl.HookReturnInfo
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.event.AttackEvent
import me.liuli.luminous.event.EventManager
import me.liuli.luminous.utils.jvm.AccessUtils
import wrapped.net.minecraft.entity.Entity

class PlayerControllerMPHook : HookFunction(AccessUtils.getObfClass("net.minecraft.client.multiplayer.PlayerControllerMP")) {
    @Hook(target = "attackEntity!(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)V", type = HookType.METHOD_ENTER, getParameters = true, returnable = true)
    fun attackEntity(returnInfo: HookReturnInfo, playerIn: Any, target: Any) {
        if(AttackEvent(Entity(target)).also { EventManager.callEvent(it) }.cancel) {
            returnInfo.cancel = true
        }
    }
}