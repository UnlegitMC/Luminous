package me.liuli.luminous.loader

import com.sun.tools.attach.VirtualMachine
import com.sun.tools.attach.VirtualMachineDescriptor
import me.liuli.luminous.Luminous
import me.liuli.luminous.utils.jvm.AttachUtils
import me.liuli.luminous.utils.misc.LogUtils
import javax.swing.*

object Loader {
    fun main(args: Array<String>) {
        LogUtils.logInfo("Welcome to ${Luminous.NAME} v${Luminous.VERSION}")

        LogUtils.logInfo("Find self jar file at: ${Luminous.jarFileAt}")

        val vm = if(args.size>=1){
            AttachUtils.getJvmById(args[0])
        }else{
            selectJvm()
        }

        if(vm == null) {
            LogUtils.logWarn("Action cancelled by user or Target JVM not found.")
            return
        }

        AttachUtils.attachJarIntoVm(vm, Luminous.jarFileAt)
        LogUtils.logWarn("Agent has been attached into target jvm.")
    }

    private fun selectJvm(): VirtualMachineDescriptor? {
        val panel = JPanel()
        panel.add(JLabel("Select target JVM:"))
        val model = DefaultComboBoxModel<String>()
        VirtualMachine.list().map { model.addElement("${it.id()} - ${it.displayName().split(" ")[0]}") }
        val comboBox = JComboBox(model)
        panel.add(comboBox)

        val result = JOptionPane.showConfirmDialog(null, panel, "${Luminous.NAME} v${Luminous.VERSION}", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)
        when (result) {
            JOptionPane.OK_OPTION -> return AttachUtils.getJvmById(comboBox.selectedItem.toString().split(" - ")[0])
        }

        return null
    }
}