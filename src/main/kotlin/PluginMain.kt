package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.constants.Permission
import com.poicraft.bot.v4.plugin.constants.WhitelistStatus
import com.poicraft.bot.v4.plugin.database.DatabaseManager
import com.poicraft.bot.v4.plugin.functions.Whitelist
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import com.poicraft.bot.v4.plugin.services.Services
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
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

        DatabaseManager.init()

        BDXWSControl.init()

        Services.init()

        val commandMap = CommandMap {
            /* 白名单 */
            /**
             * 添加白名单
             * @author gggxbbb
             */
            command("添加白名单", "addw") {
                intro("添加白名单")
                require(Permission.PERMISSION_LEVEL_ADMIN)
                onMessage { event, args ->
                    val target = args.subList(1, args.size)
                    if (target.isEmpty()) {
                        event.subject.sendMessage("请提供 Xbox ID !")
                    } else {
                        val (status, result) = Whitelist.add(target.joinToString(" "))
                        when (status) {
                            WhitelistStatus.PLAYER_ALREADY_IN_WHITELIST -> event.subject.sendMessage("玩家已在白名单中")
                            WhitelistStatus.PLAY_ADDED -> event.subject.sendMessage("已添加至白名单")
                            else -> event.subject.sendMessage("发生未知错误 $result")
                        }
                    }
                }
            }

            /**
             * 添加白名单
             * @author gggxbbb
             */
            command("删除白名单", "rmw") {
                intro("删除白名单")
                require(Permission.PERMISSION_LEVEL_ADMIN)
                onMessage { event, args ->
                    val target = args.subList(1, args.size)
                    if (target.isEmpty()) {
                        event.subject.sendMessage("请提供 Xbox ID !")
                    } else {
                        val (status, result) = Whitelist.remove(target.joinToString(" "))
                        when (status) {
                            WhitelistStatus.PLAYER_NOT_IN_WHITELIST -> event.subject.sendMessage("玩家不在白名单中")
                            WhitelistStatus.PLAY_REMOVED -> event.subject.sendMessage("已从白名单中移除")
                            else -> event.subject.sendMessage("发生未知错误 $result")
                        }
                    }
                }
            }
            /* end 白名单 */
        }


        commandMap.loadCommands { names ->
            var msg = "已加载${names.size}个命令: "
            for (name in names) {
                msg += ("$name ")
            }
            logger.info(msg.trimIndent())
        }

        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            var message = this.message.contentToString()
            if (message.startsWith("#")) {
                if (!PluginData.groupList.contains(source.group.id)) {
                    return@subscribeAlways
                }
                message = message.removePrefix("#")

                val args: List<String> = message.split(" ")

                commandMap.getCommand(message).run(this, args)
            }
        }
    }
}

object PluginData : AutoSavePluginConfig("PoiBotConf") {
    @ValueDescription("sqlite数据库的绝对位置")
    var databasePath by value("")
    var remoteConfig by value(RemoteConfig())

    @ValueDescription("机器人服务的群")
    var groupList by value<List<Long>>(listOf())
}

@Serializable
data class RemoteConfig(
    val host: String = "1.14.5.14",
    val port: Int = 1919,
    val path: String = "/abcdefg",
    val password: String = "1p1a4s5s1w4o1r9d"
)
