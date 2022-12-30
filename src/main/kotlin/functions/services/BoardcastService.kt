package com.poicraft.bot.v4.plugin.functions.services

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.PluginMain.logger
import com.poicraft.bot.v4.plugin.data.maps.damageSource
import com.poicraft.bot.v4.plugin.data.maps.damageSourceWithDamager
import com.poicraft.bot.v4.plugin.data.maps.idToValueEntity
import com.poicraft.bot.v4.plugin.provider.service.Service
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import com.poicraft.bot.v4.plugin.remote.bdxws.data.OnJoinRes
import com.poicraft.bot.v4.plugin.remote.bdxws.data.OnLeftRes
import com.poicraft.bot.v4.plugin.remote.bdxws.data.OnMobdieRes
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        var msg = ""
        var text = ""
        if (type == "minecraft:player") {
            val name = params.mobname

            if (params.srctype == "unknown") {
                //无外力失败
                text = damageSource[params.dmname] ?: "失败了"

            } else {
                // 有外力失败
                var damager = ""
                if (params.srcname.isNotEmpty()) {
                    //有名来源
                    if (params.srctype == "minecraft:player") {
                        //来源为玩家
                        damager = params.srcname
                    } else {
                        //来源为非玩家
                        var srctype = params.srctype
                        if (srctype.startsWith("minecraft:")) {
                            srctype = srctype.substring(10)
                        }
                        damager = params.srcname + "(" + (idToValueEntity[srctype] ?: params.srctype) + ")"
                    }
                } else {
                    //无名来源
                    var srctype = params.srctype
                    if (srctype.startsWith("minecraft:")) {
                        srctype = srctype.substring(10)
                    }
                    damager = (idToValueEntity[srctype] ?: params.srctype)
                }
                text = (damageSourceWithDamager[params.dmname] ?: "因%s失败了").format(damager)
            }
            msg = "$name $text"
            PluginData.groupList.forEach {
                bot.getGroup(it)!!.sendMessage(msg)
            }
        }


    }
}