package me.liuli.luminous.agent.hook.asm

import jdk.internal.org.objectweb.asm.ClassReader
import jdk.internal.org.objectweb.asm.ClassWriter
import me.liuli.luminous.Luminous
import me.liuli.luminous.agent.hook.HookManager
import java.io.File
import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain


class HookTransformer : ClassFileTransformer {
    override fun transform(
        classLoader: ClassLoader,
        name: String,
        clazz: Class<*>,
        protectionDomain: ProtectionDomain,
        byteArray: ByteArray
    ): ByteArray {
        val function = HookManager.getHookFunction(clazz) ?: return byteArray

        val cr = ClassReader(byteArray)
        val cw = ClassWriter(cr, ClassWriter.COMPUTE_FRAMES)
        val cv = HookClassVisitor(cw, function)
        cr.accept(cv, ClassReader.SKIP_FRAMES or ClassReader.SKIP_DEBUG)

        return cw.toByteArray()
    }
}