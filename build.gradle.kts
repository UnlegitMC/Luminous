plugins {
    kotlin("jvm") version "1.5.31"
    java
}

group = "me.liuli.luminous"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(files(org.gradle.internal.jvm.Jvm.current().toolsJar))
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.example.MainKt"
    }
}