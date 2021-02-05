package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import net.mamoe.mirai.event.events.MessageEvent

object EmptyCommand : Command() {
    override suspend fun onMessage(event: MessageEvent, args: List<String>) {
        return
    }
}