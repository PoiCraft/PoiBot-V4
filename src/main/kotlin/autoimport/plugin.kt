package com.poicraft.bot.v4.plugin.autoimport

import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.plugins.Plugin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.event.GroupMessageSubscribersBuilder
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages

fun GroupMessageSubscribersBuilder.loadAllPlugin() {
    getAllAnnotatedWith(Plugin::class)
        .forEach { it.call(this) }
}

@ExperimentalCoroutinesApi
fun PluginMain.loadAllPlugin() {
    globalEventChannel().subscribeGroupMessages {
        loadAllPlugin()
    }
}