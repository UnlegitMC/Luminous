package me.liuli.luminous.features.module

import me.liuli.luminous.event.*
import me.liuli.luminous.utils.jvm.AccessUtils
import org.lwjgl.input.Keyboard

object ModuleManager: Listener {
    val modules = mutableListOf<Module>()

    init {
        EventManager.registerListener(this)
        AccessUtils.resolvePackage("me.liuli.luminous.features.module.modules", Module::class.java)
            .forEach(this::registerModule)
    }

    operator fun get(name: String): Module? {
        return modules.find { it.name.equals(name, true) }
    }

    fun <T : Module> get(clazz: Class<T>): T? {
        return modules.find { it.javaClass == clazz } as T?
    }

    private fun registerModule(clazz: Class<out Module>) {
        registerModule(AccessUtils.getInstance(clazz))
    }

    fun registerModule(module: Module) {
        modules.add(module)
        EventManager.registerListener(module)
    }

    fun getModuleNames(): List<String> {
        return modules.map { it.name }
    }

    @EventHandler
    private fun onKey(event: KeyEvent) {
        modules.filter { it.triggerType == ModuleTriggerType.TOGGLE && it.keyBind == event.key }.forEach { it.toggle() }
    }

    @EventHandler
    private fun onRender2d(event: Render2DEvent) {
        modules.filter { it.triggerType == ModuleTriggerType.HOLD }.forEach { it.state = Keyboard.isKeyDown(it.keyBind) }
    }

    override val listen: Boolean
        get() = true
}