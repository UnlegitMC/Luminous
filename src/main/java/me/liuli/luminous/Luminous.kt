package me.liuli.luminous

import com.sun.tools.attach.VirtualMachine
import com.sun.tools.attach.VirtualMachineDescriptor
import com.sun.tools.attach.spi.AttachProvider

object Luminous {
    @JvmStatic
    fun main(args: Array<String>) {
        VirtualMachine.list().forEach {
            if(it.displayName().startsWith("net.minecraft")){
                println("${it.id()} ${it.displayName()}")
                attachVm(it)
            }
        }
    }

    fun attachVm(descriptor: VirtualMachineDescriptor) {
        val vm=VirtualMachine.attach(descriptor)
//        vm.
    }
}