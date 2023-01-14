package com.poicraft.bot.v4.plugin.functions.commands

import com.poicraft.bot.v4.plugin.provider.command.B
import com.poicraft.bot.v4.plugin.provider.command.Command
import com.poicraft.bot.v4.plugin.provider.command.by
import com.poicraft.bot.v4.plugin.provider.command.command
import com.poicraft.bot.v4.plugin.provider.command.reply
import com.poicraft.bot.v4.plugin.utils.minecraft.getServerInfo

/**
 * 小工具集
 */
@Command
fun B.utils() {

    /**
     * 面向新玩家的服务器相关信息
     */
    command("地址") by "addr" reply """
        服务器名称: PoiCraft
        服务器地址: play.poicraft.com
        服务器端口: 19132
    """.trimIndent()

    /**
     * 复杂回复的例子, 获得服务器状态信息
     */
    command("本服信息") by "info" reply {
        val info = getServerInfo("play.poicraft.com")
        """
            服务器名称: ${info.name}
            服务器版本: ${info.version}(${info.serverVersion})
            在线人数: ${info.player}/${info.maxPlayer}
            游戏模式: ${info.gameMode}
        """.trimIndent()
    }

    /**
     * 简单回复的例子, 确认机器人存活
     */
    command("生死检测") by "alive" reply "Bot 还活着"
}