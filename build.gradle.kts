plugins {
    kotlin("jvm") version "2.3.0" apply false
    kotlin("multiplatform") version "2.3.0" apply false
    id("io.ktor.plugin") version "3.4.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0" apply false
    id("dev.detekt") version "2.0.0-alpha.2"
}

subprojects {
    group = "leeds.compsci"
    version = "0.0.1"
}

repositories {
    mavenCentral()
}