package me.liuli.luminous.loader

import com.sun.tools.attach.VirtualMachine
import com.sun.tools.attach.VirtualMachineDescriptor
import me.liuli.luminous.Luminous
import me.liuli.luminous.loader.connect.Message
import me.liuli.luminous.loader.connect.MessageThread
import me.liuli.luminous.loader.console.Console
import me.liuli.luminous.loader.console.ConsoleCompleter
import me.liuli.luminous.utils.jvm.AttachUtils
import me.liuli.luminous.utils.misc.logError
import me.liuli.luminous.utils.misc.logWarn
import java.io.File
import javax.swing.*
import kotlin.system.exitProcess

object Loader {
    private val consoleThread = Thread {
        Console().start()
    }
    val messageThread = MessageThread()

    /**
     * called from [Luminous.main]
     */
    fun main() {
        val vm = if (System.getProperty("luminous.targetjvm") != null) {
            AttachUtils.getJvmById(System.getProperty("luminous.targetjvm"))
        } else {
            selectJvm()
        }

        if (vm == null) {
            logError("Action cancelled by user or Target JVM not found.")
            return
        }

        messageThread.start()
        consoleThread.start()

        AttachUtils.attachJarIntoVm(vm, Luminous.jarFileAt)
        logWarn("Agent has been attached into target jvm.")
    }

    /**
     * pop a JOptionPane to select a target JVM.
     */
    private fun selectJvm(): VirtualMachineDescriptor? {
        val panel = JPanel()
        panel.add(JLabel("Select target JVM:"))
        val model = DefaultComboBoxModel<String>()
        VirtualMachine.list().map { model.addElement("${it.id()} - ${it.displayName().split(" ")[0]}") }
        val comboBox = JComboBox(model)
        panel.add(comboBox)

        val result = JOptionPane.showConfirmDialog(null, panel, Luminous.TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)
        when (result) {
            JOptionPane.OK_OPTION -> return AttachUtils.getJvmById(comboBox.selectedItem.toString().split(" - ")[0])
        }

        return null
    }

    fun shutdownLoader() {
        if(consoleThread.isAlive)
            consoleThread.interrupt()

        if(messageThread.isAlive)
            messageThread.interrupt()

        logWarn("Shutting down loader...")
        exitProcess(0)
    }
}