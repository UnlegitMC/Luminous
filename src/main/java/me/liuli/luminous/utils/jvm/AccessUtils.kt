package me.liuli.luminous.utils.jvm

object AccessUtils {
    enum class MinecraftEnv {
        NOTCH,
        SEARGE
    }

    val currentEnv = try {
        Class.forName("net.minecraft.client.Minecraft")
        MinecraftEnv.NOTCH
    } catch (e: ClassNotFoundException) {
        MinecraftEnv.SEARGE
    }

//    val classMap = mutableMapOf<String, String>()
}