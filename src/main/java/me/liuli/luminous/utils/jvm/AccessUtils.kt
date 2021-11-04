package me.liuli.luminous.utils.jvm

import me.liuli.luminous.agent.Agent

object AccessUtils {
    enum class MinecraftEnv {
        NOTCH,
        SEARGE,
        MCP
    }

    val currentEnv = try {
        val clazz = getClassByName("net.minecraft.client.Minecraft")
        if(clazz.declaredFields.any { it.name == "thePlayer" }) {
            MinecraftEnv.MCP
        } else {
            MinecraftEnv.SEARGE
        }
    } catch (e: ClassNotFoundException) {
        MinecraftEnv.NOTCH
    }

    // TODO: get obfuscated class/method/field by mcp name
//    val classMap = mutableMapOf<String, String>()

    /**
     * get class by name from instrumentation classloader
     * this able to find wrapped classes
     */
    fun getClassByName(name: String)
            = Agent.instrumentation.allLoadedClasses.find { it.name == name } ?: throw ClassNotFoundException(name)
}