package com.poicraft.bot.v4.plugin.functions.commands

import com.poicraft.bot.v4.plugin.data.constants.WhitelistStatus
import com.poicraft.bot.v4.plugin.provider.command.*
import com.poicraft.bot.v4.plugin.utils.minecraft.Whitelist
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * 白名单管理
 */
@ExperimentalCoroutinesApi
@Command
fun B.whitelist() {
    /**
     * 添加白名单
     */
    commandOP("添加白名单") by "addw" run { event, args ->
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

    /**
     * 添加白名单
     */
    commandOP("删除白名单") by "rmw" run { event, args ->
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