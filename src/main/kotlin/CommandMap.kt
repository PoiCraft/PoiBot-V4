package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.commands.EmptyCommand
import com.poicraft.bot.v4.plugin.commands.Hitokoto

object CommandMap : HashMap<String, Command>() {
    fun loadCommands() {
        clear()
        put("hitokoto", Hitokoto)
        put("一言", Hitokoto)
    }

    fun getCommand(message: String): Command {
        return getOrDefault(message.split(" ")[0], EmptyCommand)
    }
}