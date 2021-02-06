package com.poicraft.bot.v4.plugin

import net.mamoe.mirai.event.events.MessageEvent

abstract class Command {
    abstract suspend fun handleMessage(event: MessageEvent, args: List<String>)
}