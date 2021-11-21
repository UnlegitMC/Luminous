package me.liuli.luminous.features.config

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import me.liuli.luminous.Luminous
import me.liuli.luminous.event.EventHandler
import me.liuli.luminous.event.EventManager
import me.liuli.luminous.event.Listener
import me.liuli.luminous.event.TickEvent
import me.liuli.luminous.utils.jvm.AccessUtils
import me.liuli.luminous.utils.misc.logInfo
import me.liuli.luminous.utils.timing.TheTimer
import java.io.File
import kotlin.text.Charsets.UTF_8

object ConfigManager: Listener {
    private val KLAXON = Klaxon()

    val configDir = File(Luminous.dataDir, "configs")
    val uniqueConfigFile = File(Luminous.dataDir, "unique.json")

    private val sections = AccessUtils.resolvePackage("me.liuli.luminous.features.config.sections", ConfigSection::class.java)
        .map { it.newInstance() }.toMutableList()

    var configName = "your_config"
        private set
    var configFile = File(configDir, "$configName.json")
        private set

    init {
        if (!configDir.exists()) {
            configDir.mkdirs()
        }

        EventManager.registerListener(this)
    }

    /**
     * load non-unique config
     */
    fun loadConfig(isUnique: Boolean) {
        logInfo("Loading config... (isUnique=$isUnique)")

        val json = readConfigJson(if(isUnique) { uniqueConfigFile } else { configFile })

        sections.filter { it.isUnique == isUnique }.forEach { section ->
            section.load(json.obj(section.name) ?: JsonObject())
        }
    }

    /**
     * write non-unique config into file
     */
    fun writeConfig(isUnique: Boolean) {
        logInfo("Saving config to disk... (isUnique=$isUnique)")

        val json = JsonObject()

        sections.filter { it.isUnique == isUnique }.forEach { section ->
            json[section.name] = JsonObject().also { section.save(it) }
        }

        writeConfigJson(if(isUnique) { uniqueConfigFile } else { configFile }, json)
    }

    /**
     * read all type of config json from file
     */
    fun loadAll() {
        loadConfig(true)
        loadConfig(false)
    }

    /**
     * write all type of config json into file
     */
    fun writeAll() {
        writeConfig(true)
        writeConfig(false)
    }

    /**
     * switch to another config
     */
    fun switchConfig(configName: String, forInitialize: Boolean = false) {
        if(!forInitialize) {
            // save current config
            writeAll()
        }

        this.configName = configName
        configFile = File(configDir, "${this.configName}.json")

        if(!forInitialize) {
            // load new config
            loadAll()
        }
    }

    private fun readConfigJson(file: File): JsonObject {
        if(!file.exists()) return JsonObject()
        return KLAXON.parseJsonObject(file.bufferedReader(UTF_8))
    }

    private fun writeConfigJson(file: File, json: JsonObject) {
        file.writeText(json.toJsonString(true), UTF_8)
    }

    private var saveScheduled = false
    private val scheduleTimer = TheTimer()

    fun scheduleSave() {
        saveScheduled = true
    }

    @EventHandler
    fun onTick(event: TickEvent) {
        if(saveScheduled && scheduleTimer.hasTimePassed(15000)) { // 15 seconds
            saveScheduled = false
            scheduleTimer.reset()
            Thread { // klaxon is not as fast as gson, so we use thread to save config
                writeAll()
                logInfo("Config saved for schedule")
            }.start()
        }
    }

    override val listen: Boolean
        get() = saveScheduled
}