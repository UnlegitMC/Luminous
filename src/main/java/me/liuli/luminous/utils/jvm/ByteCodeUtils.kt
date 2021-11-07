package me.liuli.luminous.utils.jvm

import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter

object ByteCodeUtils {
    fun writeInsnNum(mv: MethodVisitor, num: Int) {
        when (num) {
            0 -> mv.visitInsn(AdviceAdapter.ICONST_0)
            1 -> mv.visitInsn(AdviceAdapter.ICONST_1)
            2 -> mv.visitInsn(AdviceAdapter.ICONST_2)
            3 -> mv.visitInsn(AdviceAdapter.ICONST_3)
            4 -> mv.visitInsn(AdviceAdapter.ICONST_4)
            5 -> mv.visitInsn(AdviceAdapter.ICONST_5)
            else -> mv.visitIntInsn(AdviceAdapter.BIPUSH, num)
        }
    }
}