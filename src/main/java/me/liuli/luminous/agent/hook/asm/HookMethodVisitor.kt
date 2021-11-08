package me.liuli.luminous.agent.hook.asm

import jdk.internal.org.objectweb.asm.Label
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter
import me.liuli.luminous.agent.hook.impl.HookMethod
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.utils.jvm.ByteCodeUtils
import java.lang.reflect.Modifier

class HookMethodVisitor(val hookMethod: HookMethod, methodVisitor: MethodVisitor, access: Int, name: String, desc: String) : AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, desc) {
    override fun onMethodEnter() {
        if(hookMethod.info.type == HookType.METHOD_ENTER) {
            injectCall() // inject the hook if the hook type is method enter
        }
    }

    override fun onMethodExit(p0: Int) {
        super.onMethodExit(p0)
        if(hookMethod.info.type == HookType.METHOD_EXIT) {
            injectCall() // inject the hook if the hook type is method exit
        }
    }

    /**
     * inject byte code to the method for calling the hook method
     */
    private fun injectCall() {
        // inject simple hook to pass the parameters to HookManager
        mv.visitLdcInsn(hookMethod.hookFunction.targetClass.name)
        mv.visitLdcInsn(hookMethod.targetMethodName)
        mv.visitLdcInsn(hookMethod.targetMethodSign)
        if(Modifier.isStatic(hookMethod.targetMethod.modifiers)) {
            mv.visitInsn(ACONST_NULL) // pass null for static method
        } else {
            mv.visitVarInsn(ALOAD, 0) // pass the instance for dynamic method
        }
        ByteCodeUtils.writeInsnNum(mv, hookMethod.targetMethod.parameters.size)
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object")
        hookMethod.targetMethod.parameters.forEachIndexed { index, _ ->
            mv.visitInsn(DUP)
            ByteCodeUtils.writeInsnNum(mv, index)
            mv.visitVarInsn(ALOAD, index + 1)
            mv.visitInsn(AASTORE)
        }
        mv.visitMethodInsn(INVOKESTATIC, "me/liuli/luminous/agent/hook/HookManager", "invokeHookMethod", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)V")
        // inject the ifeq return check if returnable is true
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