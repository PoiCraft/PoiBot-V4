package com.poicraft.bot.v4.plugin.services

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.PluginMain.logger
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import com.poicraft.bot.v4.plugin.remote.bdxws.data.OnJoinRes
import com.poicraft.bot.v4.plugin.remote.bdxws.data.OnLeftRes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.Bot


@ExperimentalCoroutinesApi
fun loggerService(plugin: PluginMain) {
    logger.info("LoggerGroup: ${PluginData.loggerGroup}")
    BDXWSControl.addEventListener<OnJoinRes> {
        val bot = Bot.instances.last()
        val target = params.sender
        bot.getGroup(PluginData.loggerGroup)!!.sendMessage("$target 加入了游戏")
    }

    BDXWSControl.addEventListener<OnLeftRes> {
        val bot = Bot.instances.last()
        val target = params.sender
        bot.getGroup(PluginData.loggerGroup)!!.sendMessage("$target 离开了游戏")
    }
}