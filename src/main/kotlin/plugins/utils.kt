package com.poicraft.bot.v4.plugin.plugins

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.dsl.by
import com.poicraft.bot.v4.plugin.dsl.command
import com.poicraft.bot.v4.plugin.dsl.reply
import com.poicraft.bot.v4.plugin.dsl.run
import com.poicraft.bot.v4.plugin.functions.getServerInfo
import net.mamoe.mirai.contact.isAdministrator
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.contact.isOwner
import net.mamoe.mirai.contact.nameCardOrNick

/**
 * 小工具集
 */
@Plugin
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
            服务器版本: ${info.version}(${info.server_version})
            在线人数: ${info.player}/${info.max_player}
            游戏模式: ${info.game_mode}
        """.trimIndent()
    }

    /**
     * 简单回复的例子, 确认机器人存活
     */
    command("生死检测") by "alive" reply "Bot 还活着"

    /**
     * 测试命令, 检查群员的权限等级
     */
    command("权限等级") by "level" run { event, _ ->
        event.subject.sendMessage(
            """
               Level of ${event.sender.nameCardOrNick}
               Owner [ ${if (event.sender.isOwner()) "✓" else "✕"} ]
               Operator [ ${if (event.sender.isOperator()) "✓" else "✕"} ]
               Admin [ ${if (event.sender.isAdministrator()) "✓" else "✕"} ]
               Admin Group [ ${if (event.group.id == PluginData.adminGroup) "✓" else "✕"} ]
               Everyone [ ✓ ]
            """.trimIndent()
        )
    }
}