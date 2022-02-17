import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.6.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.10.0" // mirai-console version
}

mirai {
    coreVersion = "2.10.0" // mirai-core version
}

group = "com.poicraft.bot.v4"
version = "0.1.0"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    mavenCentral()
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
    implementation(group = "org.xerial", name = "sqlite-jdbc", version = "3.36.0.2")
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "io.github.classgraph", name = "classgraph", version = "4.8.139")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}