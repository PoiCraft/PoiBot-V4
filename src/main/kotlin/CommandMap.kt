package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.commands.*

/**
 * 命令表
 * @author topjohncian, gggxbbb
 */
object CommandMap : HashMap<String, BotCommand>() {

    private val commands: MutableList<BotCommand> = mutableListOf(
        Hitokoto.update(),
        Address.update(),
        Bind.update(),
        Dailypics.update(),
        BedrockTools.update(),
        Status.update(),
        Exec.update(),
        ServerTools.update(),
        Whitelist.update(),
        RandomTP.update(),
    )

    fun loadCommands(callback: (List<String>) -> Unit = { _ -> }) {
        clear()

        val helper = Helper
        helper.load(commands)
        commands.add(helper.update())

        val names = mutableListOf<String>()

        for (command in commands) {
            names.add(command.name)
            for (alias in command.aliases) {
                put(alias, command)
            }
        }

        callback(names)

    }

    fun getCommand(message: String): BotCommand {
        return getOrDefault(message.split(" ")[0], EmptyCommand.update())
    }
}