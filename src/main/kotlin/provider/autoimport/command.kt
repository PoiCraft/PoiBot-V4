package com.poicraft.bot.v4.plugin.provider.autoimport

import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.provider.command.Command
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.event.GroupMessageSubscribersBuilder
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages

fun GroupMessageSubscribersBuilder.loadAllCommand() {
    getAllAnnotatedWith(Command::class)
        .forEach { it.call(this) }
}

@ExperimentalCoroutinesApi
fun PluginMain.loadAllCommand() {
    globalEventChannel().subscribeGroupMessages {
        loadAllCommand()
    }
}