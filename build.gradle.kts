plugins {
    kotlin("jvm") version "1.5.31"
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "me.liuli.luminous"
version = "1.0.0"

val include: Configuration by configurations.creating

configurations {
    implementation.extendsFrom(include)
}

repositories {
    mavenCentral()
}

dependencies {
    include(kotlin("stdlib"))
    implementation(files(org.gradle.internal.jvm.Jvm.current().toolsJar))
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
    configurations = mutableListOf(include)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "me.liuli.luminous.Luminous"
        attributes["Agent-Class"] = "me.liuli.luminous.Luminous"
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}