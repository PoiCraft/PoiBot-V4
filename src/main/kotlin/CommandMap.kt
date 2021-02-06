package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.commands.Address
import com.poicraft.bot.v4.plugin.commands.EmptyCommand
import com.poicraft.bot.v4.plugin.commands.Helper
import com.poicraft.bot.v4.plugin.commands.Hitokoto

object CommandMap : HashMap<String, Command>() {

    private val commands:MutableList<Command> = mutableListOf(
        Hitokoto,
        Address
    )

    fun loadCommands() {
        clear()

        val helper = Helper
        helper.load(commands)
        commands.add(helper)

        for (command in commands){
            for (c in command.aliases){
                put(c, command)
            }
        }
    }

    fun getCommand(message: String): Command {
        return getOrDefault(message.split(" ")[0], EmptyCommand)
    }
}