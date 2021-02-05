package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import net.mamoe.mirai.event.events.MessageEvent

object Hitokoto : Command() {
    override suspend fun onMessage(event: MessageEvent, args: List<String>) {
        event.subject.sendMessage("Hitokoto!")
    }
}