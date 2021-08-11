package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.commands.*

/**
 * 命令表
 * @author topjohncian, gggxbbb
 */
class CommandMap(builder: CommandMap.() -> Unit) : HashMap<String, BotCommand>() {

    /**
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
     */
    private val commands: MutableList<BotCommand> = mutableListOf()

    fun loadCommands(callback: (List<String>) -> Unit = { _ -> }) {
        clear()

        val names = mutableListOf<String>()

        for (command in commands) {
            names.add(command.name)
            for (alias in command.aliases) {
                put(alias, command)
            }
        }

        callback(names)

    }

    fun command(name: String, aliases: List<String>, builder: BotCommand.() -> Unit) {
        val cmd = BotCommand(name, aliases)
        builder(cmd)
        this.commands.add(cmd)
    }

    fun command(name: String, alias: String, builder: BotCommand.() -> Unit) {
        val cmd = BotCommand(name, listOf(alias))
        builder(cmd)
        this.commands.add(cmd)
    }

    fun command(command: BotCommand) {
        this.commands.add(command)
    }

    fun getCommand(message: String): BotCommand {
        return getOrDefault(message.split(" ")[0], EmptyCommand.update())
    }

    init {
        builder(this)
    }

}