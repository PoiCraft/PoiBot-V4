package com.poicraft.bot.v4.plugin.plugins

import com.poicraft.bot.v4.plugin.command
import com.poicraft.bot.v4.plugin.constants.Permission
import com.poicraft.bot.v4.plugin.constants.WhitelistStatus
import com.poicraft.bot.v4.plugin.functions.Whitelist
import net.mamoe.mirai.event.GroupMessageSubscribersBuilder

typealias B = GroupMessageSubscribersBuilder

fun B.whitelist() {
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