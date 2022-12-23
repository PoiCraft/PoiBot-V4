package com.poicraft.bot.v4.plugin.services

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import com.poicraft.bot.v4.plugin.remote.bdxws.data.OnChatRes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.Bot

@OptIn(ExperimentalCoroutinesApi::class)
@Service
fun messageSynchronizingService(plugin: PluginMain) {

    /**
     * MC -> QQ
     */
    BDXWSControl.addEventListener<OnChatRes> {
        val bot = Bot.instances.last()
        val target = params.sender
        val message = params.text
        PluginData.groupList.forEach {
            bot.getGroup(it)!!.sendMessage("$target: $message")
        }
    }


}