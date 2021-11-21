package me.liuli.luminous.agent.jis

import me.liuli.luminous.Luminous
import me.liuli.luminous.agent.Agent
import net.minecraftforge.common.MinecraftForge
import javax.swing.JOptionPane

/**
 * use java injector to inject into jvm which disabled attach mechanism
 * https://github.com/TheQmaks/JavaInjector/
 * @author liuli
 */
class JavaInjectorAgent {
    init {
        JOptionPane.showMessageDialog(null, "Client Loaded!", "Alert", JOptionPane.ERROR_MESSAGE)

        Agent.initForForge()

        // use forge event bus to listen events, so the features may be limited
        MinecraftForge.EVENT_BUS.register(ForgeEventHandler())
        Runtime.getRuntime().addShutdownHook(Thread {
            Luminous.shutdown()
        })
    }
}