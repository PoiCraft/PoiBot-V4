package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.database.initDatabase
import com.poicraft.bot.v4.plugin.plugins.*
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import com.poicraft.bot.v4.plugin.services.initService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.utils.info
import org.ktorm.database.Database

@ExperimentalCoroutinesApi
object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.poicraft.bot.v4",
        name = "PoiBot-V4",
        version = "4.0.0"
    )
) {

    lateinit var database: Database

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

        initDatabase()

        BDXWSControl.init()

        /**
         * WebSocket 连接失败提醒
         */
        BDXWSControl.onCrash { e ->
            launch {
                Bot.instances.last().getGroup(PluginData.adminGroup)!!.sendMessage("WebSocket 炸了, 我不干了: " + e.message)
            }
        }

        initService()

        /**
         * 激活命令
         *
         * 可至 plugins/init.kt 查看命令的编写方法
         */
        GlobalEventChannel.subscribeGroupMessages {
            utils()
            whitelist()
            exec()
            status()
            help()
            bind()
        }

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
    @ValueDescription("sqlite数据库的绝对位置")
    var databasePath by value("")
    var remoteConfig by value(RemoteConfig())

    @ValueDescription("机器人服务的群")
    var groupList by value<List<Long>>(listOf())

    @ValueDescription("发送日志的群, 管理群")
    val adminGroup by value(123456L)
}

@Serializable
data class RemoteConfig(
    val host: String = "1.14.5.14",
    val port: Int = 1919,
    val path: String = "/abcdefg",
    val password: String = "1p1a4s5s1w4o1r9d"
)
