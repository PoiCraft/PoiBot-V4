package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import com.poicraft.bot.v4.plugin.database.getXboxID
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote

object Whitelist : Command() {
    override val name: String = "白名单管理"
    override val aliases: List<String> = listOf(
        "w",
        "whitelist"
    )
    override val enableSubCommand: Boolean = true

    object Add : Command() {
        override val name: String = "加白"
        override val aliases: List<String> = listOf(
            "a",
            "add"
        )
        override val argsRequired: Int = 1
        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            val target = event.getXboxID(args[1])
            if (target == null) {
                event.subject.sendMessage(event.source.quote() + "玩家未绑定")
            } else {
                val msg = when (val result = BDXWSControl.runCmd("whitelist add \"$target\"")) {
                    "Player added to whitelist" -> "添加成功"
                    "Player already in whitelist" -> "玩家已在白名单中"
                    else -> result
                }
                event.subject.sendMessage(event.source.quote() + msg)
            }
        }
    }

    object Remove : Command() {
        override val name: String = "删白"
        override val aliases: List<String> = listOf(
            "d",
            "remove"
        )
        override val argsRequired: Int = 1
        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            val target = event.getXboxID(args[1])
            if (target == null) {
                event.subject.sendMessage(event.source.quote() + "玩家未绑定")
            } else {
                val msg = when (val result = BDXWSControl.runCmd("whitelist remove \"$target\"")) {
                    "Player removed from whitelist" -> "玩家已移除"
                    "Player not in whitelist" -> "玩家未在白名单中"
                    else -> result
                }
                event.subject.sendMessage(event.source.quote() + msg)
            }
        }
    }

    init {
        newSubCommand(Add)
        newSubCommand(Remove)
    }

}