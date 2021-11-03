package me.liuli.luminous.loader

import com.sun.tools.attach.VirtualMachine
import me.liuli.luminous.Luminous
import me.liuli.luminous.utils.jvm.AttachUtils

object Loader {
    fun main(args: Array<String>) {
        println(Luminous.jarFileAt)
        VirtualMachine.list().forEach {
            if(it.displayName().startsWith("net.minecraft")){
                println("${it.id()} ${it.displayName()}")
                AttachUtils.attachJarIntoVm(it, Luminous.jarFileAt)
            }
        }
    }
}