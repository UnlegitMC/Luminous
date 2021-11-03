package me.liuli.luminous.utils.jvm

import com.sun.tools.attach.VirtualMachine
import com.sun.tools.attach.VirtualMachineDescriptor
import java.io.File

object AttachUtils {
    fun attachJarIntoVm(descriptor: VirtualMachineDescriptor, jar: File) {
        val vm = VirtualMachine.attach(descriptor)
        vm.loadAgent(jar.absolutePath, "")
        vm.detach()
    }

    fun getJvmById(id: String): VirtualMachineDescriptor? {
        return VirtualMachine.list().find { it.id() == id }
    }
}