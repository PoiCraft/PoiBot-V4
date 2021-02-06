package com.poicraft.bot.v4.plugin

import net.mamoe.mirai.event.events.GroupMessageEvent

abstract class Command {
    abstract val name: String
    abstract val aliases: List<String>
    abstract val introduction: String
    abstract suspend fun handleMessage(event: GroupMessageEvent, args: List<String>)
}