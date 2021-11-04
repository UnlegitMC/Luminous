package me.liuli.luminous.utils.jvm

object AccessUtils {
    enum class MinecraftEnv {
        NOTCH,
        SEARGE,
        VANILLA
    }

    val currentEnv = try {
        val clazz = Class.forName("net.minecraft.client.Minecraft")
        if(clazz.declaredFields.any { it.name == "thePlayer" }) {
            MinecraftEnv.VANILLA
        } else {
            MinecraftEnv.SEARGE
        }
    } catch (e: ClassNotFoundException) {
        MinecraftEnv.NOTCH
    }

//    val classMap = mutableMapOf<String, String>()
}