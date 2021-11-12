plugins {
    kotlin("jvm") version "1.5.31"
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.18.1"
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
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detekt_version")
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

detekt {
    toolVersion = "$detekt_version"
    source = files("$projectDir")
    config = files("$projectDir/detekt.yml")
    basePath = projectDir.absolutePath
    autoCorrect = true
}
tasks.getByPath("detekt").onlyIf { gradle.startParameter.taskNames.any { it.contains("detekt") } }