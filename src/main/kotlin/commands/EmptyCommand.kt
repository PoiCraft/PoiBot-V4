package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import net.mamoe.mirai.event.events.GroupMessageEvent

object EmptyCommand : Command() {
    override val name: String = "啥都没有"
    override val introduction: String = "啥都没有"
    override val aliases: List<String> = listOf()

    override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
        return
    }
}