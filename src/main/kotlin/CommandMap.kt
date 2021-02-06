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

    fun loadCommands(callback: (List<String>) -> Unit = {_->}) {
        clear()

        val helper = Helper
        helper.load(commands)
        commands.add(helper)

        val names = mutableListOf<String>()

        for (command in commands) {
            names.add(command.name)
            for (alias in command.aliases) {
                put(alias, command)
            }
        }

        callback(names)

    }

    fun getCommand(message: String): Command {
        return getOrDefault(message.split(" ")[0], EmptyCommand)
    }
}