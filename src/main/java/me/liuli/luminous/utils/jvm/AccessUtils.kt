package me.liuli.luminous.utils.jvm

import me.liuli.luminous.Luminous
import me.liuli.luminous.agent.Agent
import me.liuli.luminous.features.value.Value
import me.liuli.luminous.utils.extension.getMethodsByName
import me.liuli.luminous.utils.extension.signature
import me.liuli.luminous.utils.game.SrgUtils
import me.liuli.luminous.utils.misc.HttpUtils
import me.liuli.luminous.utils.misc.logError
import me.liuli.luminous.utils.misc.logInfo
import me.liuli.luminous.utils.misc.logWarn
import org.apache.logging.log4j.core.config.plugins.util.PluginRegistry
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil
import org.lwjgl.opengl.Display
import java.io.BufferedWriter
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Method
import javax.swing.*

object AccessUtils {
    val SRG_URL = "${Luminous.RESOURCE}/srg"
    val SUPPORT_VERSION_LIST =
        arrayOf("1.7.10",
            "1.8", "1.8.8", "1.8.9",
            "1.9", "1.9.2", "1.9.4",
            "1.10", "1.10.2",
            "1.11", "1.11.1", "1.11.2",
            "1.12", "1.12.1", "1.12.2")
    val SRG_STABLE_MAP = mutableMapOf<String, String>().also {
        it["1.7.10"] = "12"
        it["1.8"] = "18"
        it["1.8.8"] = "20"
        it["1.8.9"] = "22"
        it["1.9"] = "24"
        it["1.9.2"] = "24"
        it["1.9.4"] = "26"
        it["1.10"] = "26"
        it["1.10.2"] = "29"
        it["1.11"] = "32"
        it["1.11.1"] = "32"
        it["1.11.2"] = "32"
        it["1.12"] = "39"
        it["1.12.1"] = "39"
        it["1.12.2"] = "39"
    }

    val srgCacheDir = File(Luminous.cacheDir, "srg")

    val currentEnv = try {
        val clazz = getClassByName("net.minecraft.client.Minecraft")
        if(clazz.declaredFields.any { it.name == "thePlayer" }) {
            MinecraftEnv.MCP
        } else {
            MinecraftEnv.SEARGE
        }
    } catch (e: ClassNotFoundException) {
        MinecraftEnv.NOTCH
    }

    val classOverrideMap = mutableMapOf<String, String>() // example: net.minecraft.client.Minecraft -> ave
    val fieldOverrideMap = mutableMapOf<String, String>() // example: net/minecraft/client/Minecraft/thePlayer -> (net/minecraft/client/Minecraft/field_71439_g ->) ave/h
    val methodOverrideMap = mutableMapOf<String, String>() // like fieldOverrideMap, but for methods

    // use cache to make process faster
    private val classCache = mutableMapOf<String, Class<*>>()
    private val fieldCache = mutableMapOf<String, Field>()
    private val methodCache = mutableMapOf<String, Method>()
    private val valueCache = mutableMapOf<Class<*>, List<Value<*>>>()

    init {
        if(!srgCacheDir.exists())
            srgCacheDir.mkdirs()
    }

    fun initWithAutoVersionDetect() {
        val title = Display.getTitle()

        // try to detect the version from title
        if(title.matches(Regex("Minecraft [0-9.]{3,6}"))) {
            init(title.substring("Minecraft ".length))
        } else {
            // try to detect the version from forge
            val version = try {
                val loaderClass = getClassByName("net.minecraftforge.fml.common.Loader")
                loaderClass.getDeclaredField("MC_VERSION").get(null) as String
            } catch (e: Throwable) {
                logError("Failed to detect Minecraft version...")

                val panel = JPanel()
                panel.add(JLabel("Select your minecraft version:"))
                val model = DefaultComboBoxModel<String>()
                SUPPORT_VERSION_LIST.forEach { model.addElement(it) }
                val comboBox = JComboBox(model)
                panel.add(comboBox)

                JOptionPane.showConfirmDialog(null, panel, Luminous.TITLE, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE)

                comboBox.selectedItem.toString()
            }

            init(version)
        }
    }

    fun init(version: String) {
        logInfo("Loading srg map for Minecraft $version with $currentEnv env")

        // all env depend on the notch map
        val notchMapFile = File(srgCacheDir, "$version.srg")
        HttpUtils.downloadIfNotExists("$SRG_URL/notchsrg/$version.srg", File(srgCacheDir, "$version.srg"))

        // download notch-srg map if in notch env
        if(currentEnv == MinecraftEnv.NOTCH) {
            phaseSrgMap(notchMapFile)
        }

        // phase srg-mcp map after notch-srg map
        if(currentEnv != MinecraftEnv.MCP) {
            val mcpVersion = SRG_STABLE_MAP[version] ?: throw IllegalArgumentException("Unsupported version: $version")
            val mcpMapFile = File(srgCacheDir, "stable_$mcpVersion.srg")
            if(!mcpMapFile.exists()) {
                logWarn("Downloading MCP$mcpVersion Fields Map...")
                val fieldCsv = HttpUtils.get("$SRG_URL/mcp/${mcpVersion}_fields.csv").split("\n")
                logWarn("Downloading MCP$mcpVersion Methods Map...")
                val methodCsv = HttpUtils.get("$SRG_URL/mcp/${mcpVersion}_methods.csv").split("\n")
                logWarn("Mixing MCP$mcpVersion CSV to SRG form...")
                val writer = BufferedWriter(mcpMapFile.writer(Charsets.UTF_8))
                SrgUtils.convertSrg(notchMapFile.readLines(Charsets.UTF_8), methodCsv, fieldCsv).forEach {
                    writer.write(it)
                    writer.newLine()
                }
                writer.flush()
                writer.close()
            }
            phaseSrgMap(mcpMapFile)
        }

        flattenSrgCache()

        logInfo("${classOverrideMap.size} Classes ${methodOverrideMap.size} Methods ${fieldOverrideMap.size} Fields was loaded!")
    }

    private fun flattenSrgCache() {
        val cache = mutableMapOf<String, String>()

        fieldOverrideMap.forEach {  (patched, original) ->
            val arr = original.split("!")
            val arr1 = patched.split("!")
            cache[arr[0] + arr1.subList(1, arr1.size).joinToString("!", "!")] = original
        }

        fieldOverrideMap.clear()
        cache.forEach(fieldOverrideMap::put)
        cache.clear()

        methodOverrideMap.forEach {  (patched, original) ->
            val arr = original.split("!")
            val arr1 = patched.split("!")
            cache[arr[0] + arr1.subList(1, arr1.size).joinToString("!", "!")] = original
        }

        methodOverrideMap.clear()
        cache.forEach(methodOverrideMap::put)
    }

    private fun phaseSrgMap(srgFile: File) {
        srgFile.readLines(Charsets.UTF_8).forEach {
            val args = it.split(" ")

            when {
                it.startsWith("CL:") -> {
                    val original = args[1].replace("/" , ".")
                    val patched = args[2].replace("/" , ".")

                    if (classOverrideMap.containsKey(original)) {
                        classOverrideMap[patched] = classOverrideMap[original]!!
                        classOverrideMap.remove(original)
                    } else {
                        classOverrideMap[patched] = original
                    }
                }

                it.startsWith("FD:") -> {
                    val original = args[1].substring(0, args[1].lastIndexOf('/')).replace('/', '.') + "!" + args[1].substring(args[1].lastIndexOf('/') + 1)
                    val patched = args[2].substring(0, args[2].lastIndexOf('/')).replace('/', '.') + "!" + args[2].substring(args[2].lastIndexOf('/') + 1)

                    if (fieldOverrideMap.containsKey(original)) {
                        fieldOverrideMap[patched] = fieldOverrideMap[original]!!
                        fieldOverrideMap.remove(original)
                    } else {
                        fieldOverrideMap[patched] = original
                    }
                }

                it.startsWith("MD:") -> {
                    val original = args[1].substring(0, args[1].lastIndexOf('/')).replace('/', '.') + "!" + args[1].substring(args[1].lastIndexOf('/') + 1) + "!" + args[2]
                    val patched = args[3].substring(0, args[3].lastIndexOf('/')).replace('/', '.') + "!" + args[3].substring(args[3].lastIndexOf('/') + 1) + "!" + args[4]

                    if (methodOverrideMap.containsKey(original)) {
                        methodOverrideMap[patched] = methodOverrideMap[original]!!
                        methodOverrideMap.remove(original)
                    } else {
                        methodOverrideMap[patched] = original
                    }
                }
            }
        }
    }

    /**
     * get class by name from instrumentation classloader
     * this able to find wrapped classes
     */
    fun getClassByName(name: String)
            = if(Agent.forgeEnv) { Class.forName(name) } else { Agent.instrumentation.allLoadedClasses.find { it.name == name } ?: throw ClassNotFoundException(name) }

    fun getObfClass(name: String)
            = classCache[name] ?: getObfClassNoCache(name).also { classCache[name] = it }

    fun getObfClassNoCache(name: String)
            = getClassByName(classOverrideMap[name] ?: name)

    fun getObfField(clazz: Class<*>, name: String)
            = fieldCache[clazz.name + "!" + name] ?: getObfFieldNoCache(clazz, name).also { it.isAccessible = true ; fieldCache[clazz.name + "!" + name] = it }

    fun getObfFieldNoCache(clazz: Class<*>, name: String)
            = clazz.getDeclaredField(fieldOverrideMap[clazz.name + "!" + name]?.split("!")?.get(1) ?: name)

    fun getObfMethod(clazz: Class<*>, name: String, signature: String)
            = methodCache[clazz.name + "!" + name + "!" + signature] ?: getObfMethodNoCache(clazz, name, signature).also { it.isAccessible = true ; methodCache[clazz.name + "!" + name + "!" + signature] = it }

    fun getObfMethodNoCache(clazz: Class<*>, name: String, signature: String): Method {
        val method = methodOverrideMap[clazz.name + "!" + name + "!" + signature]
            ?: return (clazz.getMethodsByName(name).find { it.signature == signature } ?: throw NoSuchMethodException(name))

        val args = method.split("!")
        return clazz.getMethodsByName(args[1]).let {
            if (it.size>1) {
                it.find { method1 -> method1.signature == args[2].replace(".", "/") }
            } else {
                it.firstOrNull()
            }
        } ?: throw NoSuchMethodException(name)
    }

    /**
     * scan classes with specified superclass like what Reflections do but with log4j [ResolverUtil]
     * @author liulihaocai
     */
    fun <T : Any> resolvePackage(packagePath: String, clazz: Class<T>): List<Class<out T>> {
        // use resolver in log4j to scan classes in target package
        val resolver = ResolverUtil()

        // set class loader
        resolver.classLoader = clazz.classLoader

        // set package to scan
        resolver.findInPackage(object : PluginRegistry.PluginTest() {
            override fun matches(type: Class<*>?): Boolean {
                return true
            }
        }, packagePath)

        // use a list to cache classes
        val list = mutableListOf<Class<out T>>()

        for(resolved in resolver.classes) {
            // check if class is assignable from target class
            if(clazz.isAssignableFrom(resolved)) {
                // add to list
                list.add(resolved as Class<out T>)
            }
        }

        return list
    }

    fun <T : Any> getInstanceOrNull(clazz: Class<T>): T? {
        return try {
            clazz.newInstance()
        } catch (e: IllegalAccessException) {
            // this module looks like a kotlin object
            getKotlinObjectInstance(clazz)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    /**
     * get instance of a class
     * @throws IllegalAccessException if the action is failed
     */
    fun <T : Any> getInstance(clazz: Class<T>): T {
        return getInstanceOrNull(clazz) ?: throw IllegalStateException("can't get instance of $clazz")
    }

    /**
     * get kotlin object instance if not return null
     */
    fun <T : Any> getKotlinObjectInstance(clazz: Class<T>): T? {
        return try {
            clazz.getDeclaredField("INSTANCE").get(null) as T // kotlin object INSTANCE field is nonnull
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * invoke a constructor with specified parameters
     * @throws NoSuchMethodException if failed to find constructor
     */
    fun invokeConstructor(clazz: Class<*>, vararg args: Any): Any {
        try {
            val constructor = args.map { if(it is NullValue) { it.clazz } else { it.javaClass } }.let {
                clazz.getConstructor(*it.toTypedArray())
            }
            return args.map { if(it is NullValue) { null } else { it } }.let {
                constructor.newInstance(*it.toTypedArray())
            }
        } catch (e: NoSuchMethodException) {
            throw NoSuchMethodException("Cannot find constructor of $clazz")
        }
    }

    fun getValuesNoCache(obj: Any) = obj.javaClass.declaredFields.map { field ->
        field.isAccessible = true
        field.get(obj)
    }.filterIsInstance<Value<*>>()

    fun getValues(obj: Any)
            = valueCache[obj.javaClass] ?: getValuesNoCache(obj).also { valueCache[obj.javaClass] = it }

    /**
     * a class for storage null value and original parameter type
     */
    class NullValue(val clazz: Class<*>)

    enum class MinecraftEnv {
        NOTCH,
        SEARGE,
        MCP
    }
}