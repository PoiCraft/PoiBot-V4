package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import net.mamoe.mirai.event.events.MessageEvent

object Address : Command() {
    override val name: String = "地址"
    override val aliases: List<String> = listOf(
        "address",
        "ip",
        "addr",
        "地址"
    )
    override val introduction: String = "获取服务器地址"

    override suspend fun handleMessage(event: MessageEvent, args: List<String>) {
        event.subject.sendMessage(
            """
        地址: play.poicraft.com
        端口: 19132
        """.trimIndent()
        )
    }

}