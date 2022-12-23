package com.poicraft.bot.v4.plugin.services

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.PluginMain.logger
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import com.poicraft.bot.v4.plugin.remote.bdxws.data.OnJoinRes
import com.poicraft.bot.v4.plugin.remote.bdxws.data.OnLeftRes
import com.poicraft.bot.v4.plugin.remote.bdxws.data.OnMobdieParam
import com.poicraft.bot.v4.plugin.remote.bdxws.data.OnMobdieRes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.json.Json
import net.mamoe.mirai.Bot

/**
 * 广播服务
 */
@ExperimentalCoroutinesApi
@Service
fun broadcastService(plugin: PluginMain) {
    logger.info("GroupList: ${PluginData.groupList.joinToString()}")

    /**
     * 玩家进入游戏
     */
    BDXWSControl.addEventListener<OnJoinRes> {
        val bot = Bot.instances.last()
        val target = params.sender
        PluginData.groupList.forEach {
            bot.getGroup(it)!!.sendMessage("$target 加入了游戏")
        }
    }

    /**
     * 玩家离开游戏
     */
    BDXWSControl.addEventListener<OnLeftRes> {
        val bot = Bot.instances.last()
        val target = params.sender
        PluginData.groupList.forEach {
            bot.getGroup(it)!!.sendMessage("$target 离开了游戏")
        }
    }

    /**
     * 玩家发送死亡
     */
    BDXWSControl.addEventListener<OnMobdieRes> {
        val bot = Bot.instances.last()
        val type = params.mobtype
        if (type == "minecraft:player") {
            val name = params.mobname
            PluginData.groupList.forEach {
                bot.getGroup(it)!!.sendMessage("$name 失败了")
            }
        }

    }
}