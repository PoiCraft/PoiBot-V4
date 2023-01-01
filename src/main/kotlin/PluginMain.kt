package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.provider.autoimport.loadAllCommand
import com.poicraft.bot.v4.plugin.provider.autoimport.loadAllService
import com.poicraft.bot.v4.plugin.provider.command.CommandBox
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info

@ExperimentalCoroutinesApi
object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.poicraft.bot.v4",
        name = "PoiBot-V4",
        version = "4.0.0"
    )
) {

    override fun onEnable() {
        logger.info {
            """
            
             ________  ________  ___  ________  ________  ________  ________ _________        ________  ________  _________        ___      ___ ___   ___     
            |\   __  \|\   __  \|\  \|\   ____\|\   __  \|\   __  \|\  _____\\___   ___\     |\   __  \|\   __  \|\___   ___\     |\  \    /  /|\  \ |\  \    
            \ \  \|\  \ \  \|\  \ \  \ \  \___|\ \  \|\  \ \  \|\  \ \  \__/\|___ \  \_|     \ \  \|\ /\ \  \|\  \|___ \  \_|     \ \  \  /  / | \  \\_\  \   
             \ \   ____\ \  \\\  \ \  \ \  \    \ \   _  _\ \   __  \ \   __\    \ \  \       \ \   __  \ \  \\\  \   \ \  \       \ \  \/  / / \ \______  \  
              \ \  \___|\ \  \\\  \ \  \ \  \____\ \  \\  \\ \  \ \  \ \  \_|     \ \  \       \ \  \|\  \ \  \\\  \   \ \  \       \ \    / /   \|_____|\  \ 
               \ \__\    \ \_______\ \__\ \_______\ \__\\ _\\ \__\ \__\ \__\       \ \__\       \ \_______\ \_______\   \ \__\       \ \__/ /           \ \__\
                \|__|     \|_______|\|__|\|_______|\|__|\|__|\|__|\|__|\|__|        \|__|        \|_______|\|_______|    \|__|        \|__|/             \|__|
                                                                                                                                                                                
        """.trimIndent()
        }

        PluginData.reload()

        BDXWSControl.init()

        /**
         * WebSocket 连接失败提醒
         */
        BDXWSControl.onCrash { e ->
            launch {
                Bot.instances.last().getGroup(PluginData.adminGroup)!!
                    .sendMessage("WebSocket 连接失败, Bot 将退出: " + e.message)
            }
        }

        BDXWSControl.onReconnect { e, t ->
            launch {
                Bot.instances.last().getGroup(PluginData.adminGroup)!!
                    .sendMessage("WebSocket 已断开, 尝试重连(${t}): " + e.message)
            }
        }

        BDXWSControl.onReconnectSuccess {
            launch {
                Bot.instances.last().getGroup(PluginData.adminGroup)!!.sendMessage("WebSocket 重连成功")
            }
        }

        /**
         * 激活服务
         *
         * 可至 services/Service.kt 查看服务的编写方法
         */
        loadAllService()

        /**
         * 激活命令
         *
         * 可至 plugins/Plugin.kt 查看命令的编写方法
         */
        loadAllCommand()

        logger.info("AdminGroup: ${PluginData.adminGroup}")

        CommandBox.loadCommands { names ->
            var msg = "已加载${names.size}个命令: "
            for (name in names) {
                msg += ("$name ")
            }
            logger.info(msg.trimIndent())
        }
    }
}

object PluginData : AutoSavePluginConfig("PoiBotConf") {
    var remoteConfig by value(RemoteConfig())

    @ValueDescription("机器人响应指令的群")
    var groupList by value<List<Long>>(listOf())

    @ValueDescription("发送日志的群, 管理群")
    val adminGroup by value(123456L)

    @ValueDescription("同步消息的群")
    val syncGroupList by value<List<Long>>(listOf())

    @ValueDescription("广播消息的群")
    val boardCastGroupList by value<List<Long>>(listOf())

    val uptimeConfig by value(UptimeConfig())

    val httpConfig by value(HttpConfig())
}

@Serializable
data class RemoteConfig(
    val host: String = "1.14.5.14",
    val port: Int = 1919,
    val mcPort: Int = 19132,
    val path: String = "/abcdefg",
    val password: String = "1p1a4s5s1w4o1r9d"
)

@Serializable
data class UptimeConfig(
    val url: String = "",
    val pingInterval: Int = 600
)

@Serializable
data class HttpConfig(
    val port: Int = 1536,
    val alertManagerWebhook: String = "/alert_manager",
    val credential: String = "",
    val password: String = "",
)