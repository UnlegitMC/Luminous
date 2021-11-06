package me.liuli.luminous.agent.hook.asm

import jdk.internal.org.objectweb.asm.Label
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter
import me.liuli.luminous.agent.hook.impl.HookMethod
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.utils.jvm.ByteCodeUtils

class HookMethodVisitor(val hookMethod: HookMethod, methodVisitor: MethodVisitor, access: Int, name: String, desc: String) : AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, desc) {
    override fun onMethodEnter() {
        if(hookMethod.info.type == HookType.METHOD_ENTER) {
            injectCall()
        }
    }

    override fun onMethodExit(p0: Int) {
        super.onMethodExit(p0)
        if(hookMethod.info.type == HookType.METHOD_EXIT) {
            injectCall()
        }
    }

    private fun injectCall() {
        mv.visitLdcInsn(hookMethod.hookFunction.targetClass.name)
        mv.visitLdcInsn(hookMethod.targetMethodName)
        mv.visitLdcInsn(hookMethod.targetMethodSign)
        mv.visitVarInsn(ALOAD, 0)
        if(hookMethod.targetMethod.parameters.isEmpty()) {
            mv.visitInsn(ICONST_0)
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object")
        } else {
            ByteCodeUtils.writeInsnNum(mv, hookMethod.targetMethod.parameters.size)
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object")
            for(i in 0 until hookMethod.targetMethod.parameters.size) {
                mv.visitInsn(DUP)
                ByteCodeUtils.writeInsnNum(mv, i)
                mv.visitVarInsn(ALOAD, i + 1)
                mv.visitInsn(AASTORE)
            }
        }
        mv.visitMethodInsn(INVOKESTATIC, "me/liuli/luminous/agent/hook/HookManager", "invokeHookMethod", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)V")
        // returnable
        if(hookMethod.info.returnable) {
            val label=Label()
            mv.visitMethodInsn(INVOKESTATIC, "me/liuli/luminous/agent/hook/HookManager", "getCache", "()Lme/liuli/luminous/agent/hook/impl/HookReturnInfo;")
            mv.visitMethodInsn(INVOKEVIRTUAL, "me/liuli/luminous/agent/hook/impl/HookReturnInfo", "getCancel", "()Z")
            mv.visitJumpInsn(IFEQ, label)
            if (hookMethod.targetMethod.returnType == Void.TYPE) {
                mv.visitInsn(RETURN)
            } else {
                mv.visitMethodInsn(INVOKESTATIC, "me/liuli/luminous/agent/hook/HookManager", "getCache", "()Lme/liuli/luminous/agent/hook/impl/HookReturnInfo;")
                mv.visitMethodInsn(INVOKEVIRTUAL, "me/liuli/luminous/agent/hook/impl/HookReturnInfo", "getReturnValue", "()Ljava/lang/Object;")
                val type = hookMethod.targetMethod.returnType
                val name = type.name.replace(".", "/")
                var flag = ""
                val cast = when (name) {
                    "boolean","byte","char","double","float","int","long","short","void" -> {
                        flag = name.substring(0,1).uppercase()+name.substring(1)
                        "java.lang.$flag"
                    }
                    else -> name
                }.replace(".","/")
                mv.visitTypeInsn(CHECKCAST, cast)
                if(flag.isNotEmpty()) {
                    mv.visitMethodInsn(INVOKEVIRTUAL, cast, "${name}Value",
                        "()" + java.lang.reflect.Array.newInstance(type, 0).toString().let { it.substring(1, it.indexOf('@')) })
                    mv.visitInsn(IRETURN)
                } else {
                    mv.visitInsn(ARETURN)
                }
            }
            mv.visitLabel(label)
        }
    }
}