package me.liuli.luminous.agent

import javax.swing.JOptionPane

/**
 * use java injector to inject into jvm which disabled attach mechanism
 * https://github.com/TheQmaks/JavaInjector/
 * @author liuli
 */
class JavaInjectorAgent {
    init {
        // TODO: add support for java injector
        JOptionPane.showMessageDialog(null, "Sorry, Java Injector is not supported now.", "Alert", JOptionPane.ERROR_MESSAGE)
    }
}