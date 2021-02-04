package com.poicraft.bot.v4.plugin

import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

@ConsoleExperimentalApi
suspend fun main() {
    MiraiConsoleTerminalLoader.startAsDaemon()

    PluginMain.load()
    PluginMain.enable()

    val env = System.getenv()
    val bot = env["ID"]?.let {
        MiraiConsole.addBot(it.toLong(), env["PASSWORD"]!!) {
        fileBasedDeviceInfo()
    }.alsoLogin()
    }

    MiraiConsole.job.join()
}