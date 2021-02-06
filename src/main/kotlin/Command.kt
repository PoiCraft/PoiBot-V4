package com.poicraft.bot.v4.plugin

import net.mamoe.mirai.event.events.MessageEvent

abstract class Command {
    abstract val name: String
    abstract val commands: List<String>
    abstract val introduction: String
    abstract suspend fun handleMessage(event: MessageEvent, args: List<String>)
}