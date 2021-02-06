package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import net.mamoe.mirai.event.events.MessageEvent

object Hitokoto : Command() {
    override val name: String = "一言"
    override val commands: List<String> = listOf(
        "一言",
        "Hitokoto"
    )
    override val introduction: String = "从网络获取一条一言"
    override suspend fun onMessage(event: MessageEvent, args: List<String>) {
        event.subject.sendMessage("Hitokoto!")
    }
}