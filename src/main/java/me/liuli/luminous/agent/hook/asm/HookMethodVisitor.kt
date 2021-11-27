package me.liuli.luminous.agent.hook.asm

import jdk.internal.org.objectweb.asm.Label
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter
import me.liuli.luminous.agent.hook.impl.HookMethod
import me.liuli.luminous.agent.hook.impl.HookType
import me.liuli.luminous.utils.extension.signature
import me.liuli.luminous.utils.jvm.ByteCodeUtils
import java.lang.reflect.Modifier

class HookMethodVisitor(val hookMethodList: List<HookMethod>, methodVisitor: MethodVisitor, access: Int, name: String, desc: String) : AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, desc) {
    override fun onMethodEnter() {
        hookMethodList.filter { it.info.type == HookType.METHOD_ENTER }.forEach {
            injectCall(it) // inject the hook if the hook type is method enter
        }
    }

    override fun onMethodExit(p0: Int) {
        super.onMethodExit(p0)
        hookMethodList.filter { it.info.type == HookType.METHOD_EXIT }.forEach {
            injectCall(it) // inject the hook if the hook type is method enter
        }
    }

    /**
     * inject byte code to the method for calling the hook method
     */
    private fun injectCall(hookMethod: HookMethod) {
        // inject simple hook to pass the parameters to HookManager
        mv.visitLdcInsn(hookMethod.id)
        var i = 0
        if(Modifier.isStatic(hookMethod.targetMethod.modifiers)) {
            mv.visitInsn(ACONST_NULL) // pass null for static method
        } else {
            mv.visitVarInsn(ALOAD, 0) // pass the instance for dynamic method
            i++
        }
        ByteCodeUtils.writeInsnNum(mv, hookMethod.targetMethod.parameters.size)
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object")
        hookMethod.targetMethod.parameters.forEachIndexed { index, parameter ->
            mv.visitInsn(DUP)
            ByteCodeUtils.writeInsnNum(mv, index)
            when(parameter.type.name) {
                "int" -> {
                    mv.visitVarInsn(ILOAD, i)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }
                "long" -> {
                    mv.visitVarInsn(LLOAD, i)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;")
                }
                "float" -> {
                    mv.visitVarInsn(FLOAD, i)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;")
                }
                "double" -> {
                    mv.visitVarInsn(DLOAD, i)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;")
                    i++
                }
                "char" -> {
                    mv.visitVarInsn(ILOAD, i)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;")
                }
                "byte" -> {
                    mv.visitVarInsn(ILOAD, i)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;")
                }
                "short" -> {
                    mv.visitVarInsn(ILOAD, i)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;")
                }
                "boolean" -> {
                    mv.visitVarInsn(ILOAD, i)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;")
                }
                else -> mv.visitVarInsn(ALOAD, i)
            }
            i++
            mv.visitInsn(AASTORE)
        }
        mv.visitMethodInsn(INVOKESTATIC, "me/liuli/luminous/agent/hook/HookManager", "invokeHookMethod", "(Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)V")
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
                    when(name) {
                        "double" -> mv.visitInsn(DRETURN)
                        "float" -> mv.visitInsn(FRETURN)
                        "long" -> mv.visitInsn(LRETURN)
                        else -> mv.visitInsn(IRETURN)
                    }
                } else {
                    mv.visitInsn(ARETURN)
                }
            }
            mv.visitLabel(label)
        }
    }
}