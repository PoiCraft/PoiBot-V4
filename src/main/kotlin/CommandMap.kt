package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.commands.Address
import com.poicraft.bot.v4.plugin.commands.EmptyCommand
import com.poicraft.bot.v4.plugin.commands.Helper
import com.poicraft.bot.v4.plugin.commands.Hitokoto

/**
 * 命令表
 * @author topjohncian, gggxbbb
 */
object CommandMap : HashMap<String, Command>() {

    private val commands: MutableList<Command> = mutableListOf(
        Hitokoto,
        Address
    )

    fun loadCommands() {
        clear()

        val helper = Helper
        helper.load(commands)
        commands.add(helper)

        for (command in commands) {
            for (alias in command.aliases) {
                put(alias, command)
            }
        }
    }

    fun getCommand(message: String): Command {
        return getOrDefault(message.split(" ")[0], EmptyCommand)
    }
}