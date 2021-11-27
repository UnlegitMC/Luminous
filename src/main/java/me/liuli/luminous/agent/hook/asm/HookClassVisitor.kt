package me.liuli.luminous.agent.hook.asm

import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes
import me.liuli.luminous.agent.hook.impl.HookFunction

class HookClassVisitor(classVisitor: ClassVisitor, val function: HookFunction) : ClassVisitor(Opcodes.ASM5, classVisitor) {
    override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        val hookMethodList = function.getHookMethods(name, desc)
        if(hookMethodList.isEmpty()) {
            return methodVisitor
        }

        return HookMethodVisitor(hookMethodList, methodVisitor, access, name, desc)
    }
}