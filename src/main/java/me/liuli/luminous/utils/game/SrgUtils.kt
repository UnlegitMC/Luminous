package me.liuli.luminous.utils.game

object SrgUtils {
    fun convertSrg(notchSrg: List<String>, methodCsv: List<String>, fieldCsv: List<String>): List<String> {
        val notchMethodMap = mutableMapOf<String, String>()
        val notchFieldMap = mutableMapOf<String, String>()
        val mcpMethodMap = mutableMapOf<String, String>()
        val mcpFieldMap = mutableMapOf<String, String>()

        // read string into map
        readSrg(notchSrg, notchMethodMap, notchFieldMap)
        readCsv(methodCsv, fieldCsv, mcpMethodMap, mcpFieldMap)

        // output the result
        return outputSrg(notchMethodMap, notchFieldMap, mcpMethodMap, mcpFieldMap)
    }

    private fun readSrg(notchSrg: List<String>, methodMap: MutableMap<String, String>, fieldMap: MutableMap<String, String>) {
        notchSrg.forEach {
            val args = it.split(" ")
            when {
                // CL is useless
                it.startsWith("FD:") -> {
                    fieldMap[args[1]] = args[2]
                }

                it.startsWith("MD:") -> {
                    methodMap[args[1] + " " + args[2]] = args[3] + " " + args[4]
                }
            }
        }
    }

    private fun readCsv(methodCsv: List<String>, fieldCsv: List<String>, methodMap: MutableMap<String, String>, fieldMap: MutableMap<String, String>) {
        methodCsv.forEachIndexed { index, str ->
            if(index != 0 && str.isNotEmpty()) {
                val split = str.split(",")
                methodMap[split[0]] = split[1]
            }
        }
        fieldCsv.forEachIndexed { index, str ->
            if(index != 0 && str.isNotEmpty()) {
                val split = str.split(",")
                fieldMap[split[0]] = split[1]
            }
        }
    }

    private fun outputSrg(notchMethodMap: MutableMap<String, String>, notchFieldMap: MutableMap<String, String>, mcpMethodMap: MutableMap<String, String>, mcpFieldMap: MutableMap<String, String>): List<String> {
        val result = mutableListOf<String>()

        notchMethodMap.forEach { (notchName, srgName) ->
            val path = srgName.split(" ")[0].let { it.substring(it.lastIndexOf("/") + 1) }
            if(mcpMethodMap.containsKey(path)) {
                result.add("MD: $srgName ${srgName.replace(path, mcpMethodMap[path]!!)}")
            }
        }

        notchFieldMap.forEach { (notchName, srgName) ->
            val path = srgName.substring(srgName.lastIndexOf("/") + 1)
            if(mcpFieldMap.containsKey(path)) {
                result.add("FD: $srgName ${srgName.replace(path, mcpFieldMap[path]!!)}")
            }
        }

        return result
    }
}