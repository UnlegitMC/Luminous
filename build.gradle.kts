plugins {
    kotlin("jvm") version "1.5.31"
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "me.liuli.luminous"
version = "1.0.0"

val include: Configuration by configurations.creating
val detekt_version = "1.18.1"

configurations {
    implementation.extendsFrom(include)
}

repositories {
    mavenCentral()
}

dependencies {
    include(kotlin("stdlib"))
    include("com.beust:klaxon:5.5")
    include("net.minecrell:terminalconsoleappender:1.3.0")
    include("org.jline:jline-terminal-jansi:3.20.0")
    implementation("org.lwjgl.lwjgl:lwjgl:2.9.3") // this dependency is included in Minecraft environment
    implementation(files(org.gradle.internal.jvm.Jvm.current().toolsJar)) // this dependency will be loaded dynamically
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
    configurations = mutableListOf(include)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    exclude("**/Log4j2Plugins.dat")
    exclude("**/module-info.class")

    manifest {
        attributes["Main-Class"] = "me.liuli.luminous.Luminous"
        attributes["Agent-Class"] = "me.liuli.luminous.Luminous"
        attributes["Can-Redefine-Classes"] = true
        attributes["Can-Retransform-Classes"] = true
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

tasks.register("genWrapper") {
    doFirst {
        var curClass = ""
        File(project.projectDir, "LumiRes/wrap.map").forEachLine {
            val split = it.split(" ")
            if (split.size == 2) {
                wrapMap[split[0]] = WrappedClass(split[0].replace("/", "."), split[1])
                curClass = split[0]
            } else if (split.size == 3) {
                if (split[1].contains("!")) {
                    wrapMap[curClass]?.methods?.add(WrappedMethod(split[0].toInt(), split[1], split[2], emptyList()))
                } else {
                    wrapMap[curClass]?.fields?.add(WrappedField(split[0].toInt(), split[1], split[2]))
                }
            } else if(split.size == 4) {
                wrapMap[curClass]?.methods?.add(WrappedMethod(split[0].toInt(), split[1], split[2], split[3].split(";")))
            }
        }
    }
    doLast {
        val wrapDir = File(project.projectDir, "src/main/kotlin/wrapped")
        if (wrapDir.exists()) wrapDir.deleteRecursively()
        wrapMap.forEach { (dir, wc) ->
            val file = File(wrapDir, "$dir.kt")
            if(!file.parentFile.exists()) file.parentFile.mkdirs()
            wc.writeTo(file)
        }
    }
}
val wrapMap = mutableMapOf<String, WrappedClass>()

class WrappedField(val modifier: Int, val name: String, val type: String) {
    val isStatic = modifier and 8 != 0
    val isFinal = modifier and 16 != 0
}
class WrappedMethod(val modifier: Int, val name: String, val type: String, val args: List<String>) {
    val isStatic = modifier and 8 != 0
}
class WrappedClass(val name: String, val superclass: String) {
    val fields = mutableListOf<WrappedField>()
    val methods = mutableListOf<WrappedMethod>()
    val superType: String
        get() = processType(superclass)

    fun writeTo(file: File) {
        val sb = StringBuilder()

        sb.append("package wrapped.${name.substring(0, name.lastIndexOf("."))}\n\n")
        sb.append("import me.liuli.luminous.wrapper.WrapManager\n\n")
        sb.append("open class ${name.substring(name.lastIndexOf(".") + 1)}(${if(superType.startsWith("wrapped")) {""} else {"val "}}theInstance: Any)${if(superType.startsWith("wrapped")) {":$superType(theInstance)"}else{""}} {\n")
        fields.forEach { putField(sb, it, false) }
        methods.forEach { putMethod(sb, it, false) }
        sb.append("\tcompanion object {\n")
        fields.forEach { putField(sb, it, true) }
        methods.forEach { putMethod(sb, it, true) }
        sb.append("\t}\n")
        sb.append("}")

        file.writeText(sb.toString())
    }

    private fun putField(sb: StringBuilder, wf: WrappedField, isForStatic: Boolean) {
        if(isForStatic != wf.isStatic) return
        val type = processType(wf.type)
        val instance = if(wf.isStatic) "null" else "theInstance"
        if(type.isNotEmpty()) {
            sb.append("${if (isForStatic) {"\t\t"} else {"\t"}}${if (wf.isFinal) {"val"} else {"var"}} ${wf.name}: $type? ")
            putJvmName(sb, "F_GET_${wf.modifier}_${wf.name}")
            sb.append("get() { return ${processWrapHead(type)}WrapManager.getter($instance, \"$name\", \"${wf.name}\")${processWrapTail(type)} }")
            if(!wf.isFinal) {
                putJvmName(sb, "F_SET_${wf.modifier}_${wf.name}")
                sb.append("set(value) { WrapManager.setter($instance, \"$name\", \"${wf.name}\", value${processWrapGet(type)}) }")
            }
            sb.append("\n")
        }
    }

    private fun putMethod(sb: StringBuilder, wm: WrappedMethod, isForStatic: Boolean) {
        if(isForStatic != wm.isStatic) return
        val type = processType(wm.type).let { if(it == "void") "Unit" else it }
        val instance = if(wm.isStatic) "null" else "theInstance"
        val split = wm.name.split("!")
        var cnt = -1
        var argsStr = ""
        val args = wm.args.joinToString(", ") {
            cnt++
            argsStr+="p$cnt,"
            "p$cnt: ${processType(it).let { if(it.isEmpty()) { "Any" }else{ it } }}?"
        }
        if(type.isNotEmpty() && !hasMethod(wm.name)) {
            sb.append(if (isForStatic) {"\t\t"} else {"\t"})
            putJvmName(sb, "M_${wm.modifier}_${split[0]}")
            sb.append("fun ${split[0]}($args)")
            if(type!="Unit") sb.append(": $type?")
            sb.append("{ ${if(type!="Unit"){"return"}else{""}} ${processWrapHead(type)}WrapManager.call($instance, \"$name\", \"${split[0]}\", \"${split[1].replace("\$", "\\\$")}\"")
            if(args.isNotEmpty()) sb.append(", ${argsStr.substring(0, argsStr.length-1)}")
            sb.append(")${if(type!="Unit"){processWrapTail(type)}else{""}} }\n")
        }
    }

    private fun putJvmName(sb: StringBuilder, tag: String) {
        sb.append("@JvmName(\"$tag\") ")
//        sb.append("@JvmName(\"$tag${Math.random().toString().split(".")[1]}\") ")
    }

    private fun processType(type: String): String {
        if(type.contains("google") || type.startsWith("[") || type.contains(";")) {
            return ""
        } else if(type.startsWith("Array<")) {
            val t = processType(type.substring(6, type.length - 1))
            return if(t.isNotEmpty()) "Array<$t>" else ""
        } else if(type.contains(".")) {
            wrapMap[type.replace(".", "/")]?.let {
                return "wrapped.${it.name}"
            }
            return try {
                Class.forName(if(type.endsWith("<*>")) type.substring(0, type.length - 3) else type)
                type
            } catch (e: ClassNotFoundException) {
                ""
            }
        } else {
            return type
        }
    }

    private fun processWrapHead(type: String): String {
        return if(type.startsWith("wrapped.")) {
            "$type("
        } else {
            ""
        }
    }

    private fun processWrapTail(type: String): String {
        return if(type.startsWith("wrapped.")) {
            "?:return null)"
        } else if(type != "void") {
            " as $type?"
        } else {
            ""
        }
    }

    private fun processWrapGet(type: String): String {
        return if(type.startsWith("wrapped.")) {
            "?.theInstance"
        } else {
            ""
        }
    }

    private fun hasMethod(name: String, recursive: Boolean = false): Boolean {
        if(superType.startsWith("wrapped") && wrapMap[superclass.replace(".", "/")]!!.hasMethod(name, true)) {
            return true
        }
        return recursive && methods.any { it.name == name }
    }
}