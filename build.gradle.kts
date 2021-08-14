import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.4.32"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.6.7" // mirai-console version
}

mirai {
    coreVersion = "2.6.7" // mirai-core version
}

group = "com.poicraft.bot.v4"
version = "0.1.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

dependencies {
    val ktorVersion = "1.5.1"
    val ktormVersion = "3.3.0"
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("com.github.oshi:oshi-core:5.8.0")
    implementation("org.ktorm:ktorm-core:$ktormVersion")
    implementation(group = "org.xerial", name = "sqlite-jdbc", version = "3.34.0")
    implementation(kotlin("stdlib-jdk8"))
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}