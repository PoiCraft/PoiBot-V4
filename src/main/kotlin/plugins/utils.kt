package com.poicraft.bot.v4.plugin.plugins

import com.poicraft.bot.v4.plugin.*
import net.mamoe.mirai.contact.isAdministrator
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.contact.isOwner
import net.mamoe.mirai.contact.nameCardOrNick

/**
 * 小工具集
 */
fun B.utils() {
    command("地址") by listOf("addr", "address") reply """
        服务器名称: PoiCraft
        服务器地址: play.poicraft.com
        服务器端口: 19132
    """.trimIndent()
    command("生死检测") by "alive" reply "Bot 还活着"
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