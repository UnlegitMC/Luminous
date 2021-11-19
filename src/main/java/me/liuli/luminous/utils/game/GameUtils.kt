package me.liuli.luminous.utils.game

import com.beust.klaxon.JsonObject
import me.liuli.luminous.Luminous
import me.liuli.luminous.utils.extension.invoke
import me.liuli.luminous.utils.extension.invokeObfMethod
import me.liuli.luminous.utils.jvm.AccessUtils
import me.liuli.luminous.utils.misc.logWarn
import wrapped.net.minecraft.client.Minecraft

val mc = Minecraft.theMinecraft!!

object GameUtils {
    private val iChatComponent = AccessUtils.getObfClass("net.minecraft.util.IChatComponent\$Serializer")

    fun displayAlert(message: String) {
        logWarn("[CHAT] $message")
        addChatMessage("§7[${Luminous.NAME_WITH_COLOR}§7] §f$message")
    }

    fun displayChat(message: String) {
        logWarn("[CHAT] $message")
        addChatMessage(message)
    }

    private fun addChatMessage(message: String) {
        Minecraft.theMinecraft!!.thePlayer?.theInstance
            ?.invoke("addChatMessage", "(Lnet/minecraft/util/IChatComponent;)V",
                iChatComponent.invokeObfMethod("jsonToComponent", "(Ljava/lang/String;)Lnet/minecraft/util/IChatComponent;",
                    JsonObject().also { it["text"] = message }.toJsonString()))
    }
}