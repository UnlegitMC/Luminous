package me.liuli.luminous.wrapper.impl.client

import me.liuli.luminous.wrapper.EnumWrapType
import me.liuli.luminous.wrapper.WrapManager
import me.liuli.luminous.wrapper.Wrapper

@Wrapper(EnumWrapType.CLASS, "net.minecraft.client.Minecraft")
object Minecraft {
    @Wrapper(EnumWrapType.METHOD, "getMinecraft!()Lnet/minecraft/client/Minecraft;")
    fun getMinecraft() = WrapManager.access()!!

    val thePlayer: Any?
        @Wrapper(EnumWrapType.FIELD, "thePlayer") get() = WrapManager.access(instance = getMinecraft())

    val theWorld: Any?
        @Wrapper(EnumWrapType.FIELD, "theWorld") get() = WrapManager.access(instance = getMinecraft())
}