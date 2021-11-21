import java.util.jar.JarFile
import java.io.FileOutputStream
import java.util.jar.JarOutputStream

plugins {
    kotlin("jvm") version "1.6.0"
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "me.liuli.luminous"
version = "1.0.0"

val include: Configuration by configurations.creating
val detekt_version = "1.18.1"
val wrapDir = File(project.projectDir, "src/main/kotlin/wrapped")

configurations {
    implementation.extendsFrom(include)
}

repositories {
    mavenCentral()
    maven("https://lumires.getfdp.today/repo/")
}

dependencies {
    include(kotlin("stdlib"))
    include("com.beust:klaxon:5.5")
    include("net.minecrell:terminalconsoleappender:1.3.0")
    include("org.jline:jline-terminal-jansi:3.20.0")
    implementation("org.lwjgl.lwjgl:lwjgl:2.9.3") // this dependency is included in Minecraft environment
    implementation("net.minecraftforge:forge:1.8.9-11.15.1.1875") // this dependency is used to support JavaInjector
    implementation(files(org.gradle.internal.jvm.Jvm.current().toolsJar)) // this dependency will be loaded dynamically
}

tasks.register("commentJar") {
    doFirst {
        val jos = JarOutputStream(FileOutputStream(File(project.buildDir, "libs/${project.name}-${project.version}-COMMENT.jar")))
        JarFile(File(project.buildDir, "libs/${project.name}-${project.version}.jar")).use { jar ->
            jar.entries().asSequence().forEach {
                jos.putNextEntry(it)
                jos.write(jar.getInputStream(it).readBytes())
                jos.closeEntry()
            }
            jar.close()
        }
        jos.setComment("me.liuli.luminous.agent.jis.JavaInjectorAgent") // for JavaInjector to locate the main class
        jos.close()
    }

    dependsOn(tasks.named("build"))
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
    configurations = mutableListOf(include)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    exclude("**/Log4j2Plugins.dat")
    exclude("**/module-info.class")
    exclude("**/package-info.class")

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
        wrapMap.forEach { (dir, wc) ->
            val file = File(wrapDir, "$dir.kt")
            if(!file.parentFile.exists()) file.parentFile.mkdirs()
            wc.writeTo(file)
        }
    }
}

tasks.register("cleanWrapper") {
    doFirst {
        if (wrapDir.exists()) wrapDir.deleteRecursively()
    }
}

tasks.named("genWrapper").get().dependsOn(tasks.named("cleanWrapper"))

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
        sb.append("import me.liuli.luminous.utils.jvm.AccessUtils\n\n")
        sb.append("open class ${name.substring(name.lastIndexOf(".") + 1)}(${if(superType.startsWith("wrapped")) {""} else {"val "}}theInstance: Any = originalClass.newInstance())${if(superType.startsWith("wrapped")) {":$superType(theInstance)"}else{""}} {\n")
        fields.forEach { putField(sb, it, false) }
        methods.forEach { putMethod(sb, it, false) }
        sb.append("\tcompanion object {\n")
        sb.append("\t\tval className = \"$name\"\n")
        sb.append("\t\tval originalClass = AccessUtils.getObfClass(className)\n")
        fields.forEach { putField(sb, it, true) }
        methods.forEach { putMethod(sb, it, true) }
        sb.append("\t}\n")
        sb.append("}")

        file.writeText(sb.toString())
    }

    private fun putField(sb: StringBuilder, wf: WrappedField, isForStatic: Boolean) {
        if(isForStatic != wf.isStatic) return
        val type = processType(wf.type)
        if(type.isNotEmpty()) {
            val instance = if(wf.isStatic) "null" else "theInstance"
            sb.append(if (isForStatic) {"\t\t"} else {"\t"})
            sb.append("${if (wf.isFinal) {"val"} else {"var"}} ${wf.name}: $type? ")
            putJvmName(sb, "G${wf.modifier}_${wf.name}")
            sb.append("get() { return ${processWrapHead(type)}AccessUtils.getObfField(originalClass, \"${wf.name}\").get($instance)${processWrapTail(type)} }")
            if(!wf.isFinal) {
                putJvmName(sb, "S${wf.modifier}_${wf.name}")
                sb.append("set(value) { AccessUtils.getObfField(originalClass, \"${wf.name}\").set($instance, value${processWrapGet(type)}) }")
            }
            sb.append("\n")
        }
    }

    private fun putMethod(sb: StringBuilder, wm: WrappedMethod, isForStatic: Boolean) {
        if(isForStatic != wm.isStatic) return
        val type = processType(wm.type).let { if(it == "void") "Unit" else it }
        val split = wm.name.split("!")
        var cnt = -1
        var argsStr = ""
        val args = wm.args.joinToString(", ") {
            cnt++
            val typ = processType(it)
            argsStr+="p$cnt${processWrapGet(typ)},"
            "p$cnt: ${typ.ifEmpty { "Any" }}?"
        }
        if(type.isNotEmpty() && !hasMethod(wm.name)) {
            val instance = if(wm.isStatic) "null" else "theInstance"
            sb.append(if (isForStatic) {"\t\t"} else {"\t"})
            putJvmName(sb, "M${wm.modifier}${split[0]}${Math.random().toString().substring(2,5)}")
            sb.append("fun ${split[0]}($args)")
            if(type!="Unit") sb.append(": $type?")
            sb.append("{ ${if(type!="Unit"){"return"}else{""}} ${processWrapHead(type)}AccessUtils.getObfMethod(originalClass, \"${split[0]}\", \"${split[1].replace("\$", "\\\$")}\").invoke($instance")
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