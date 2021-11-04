package me.liuli.luminous.agent

import java.lang.instrument.Instrumentation

object Agent {
    lateinit var instrumentation: Instrumentation
        private set

    fun main(agentArgs: String, instrumentation: Instrumentation) {
        this.instrumentation = instrumentation
    }
}