package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.database.DatabaseManager
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.info

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.poicraft.bot.v4",
        name = "PoiBot-V4",
        version = "0.1.0"
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

        CommandMap.loadCommands { names ->
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
                    subject.sendMessage("本群不处理事件！")
                    return@subscribeAlways
                }
                message = message.removePrefix("#")

                var longArg = false
                val args: MutableList<String> = mutableListOf()
                message.split(" ").forEach {
                    if (longArg) {
                        if (it.endsWith("\"")) {
                            longArg = false
                            args[args.size - 1] += " ${it.dropLast(1)}"
                        } else {
                            args[args.size - 1] += " $it"
                        }
                    } else {
                        if (it.isNotBlank()) {
                            if (it.startsWith("\"")) {
                                longArg = true
                                args.add(it.drop(1))
                            } else {
                                args.add(it)
                            }
                        }
                    }

                }

                CommandMap.getCommand(message)
                    .onMessage(this, args)
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
