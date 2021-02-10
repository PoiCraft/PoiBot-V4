package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.database.DatabaseManager
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
                message = message.removePrefix("#")
                val args = message.split(" ")

                CommandMap.getCommand(message)
                    .onMessage(this, args)
            }
        }
    }
}

object PluginData : AutoSavePluginConfig("PoiBotConf") {
    @ValueDescription("sqlite数据库的绝对位置")
    var databasePath by value("")
}