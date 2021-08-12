package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.PluginMain.commandMap
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent

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

    /**
     * 构造命令
     * @param name 命令的人类友好名称
     * @param aliases 命令的程序友好名称
     */
    @MessageDsl
    fun command(name: String, aliases: List<String>, builder: BotCommand.() -> Unit) {
        val cmd = BotCommand(name, aliases)
        builder(cmd)
        this.commands.add(cmd)
    }

    /**
     * 构造命令
     * @param name 命令的人类友好名称
     * @param alias 命令的程序友好名称
     */
    @MessageDsl
    fun command(name: String, alias: String, builder: BotCommand.() -> Unit) {
        val cmd = BotCommand(name, listOf(alias))
        builder(cmd)
        this.commands.add(cmd)
    }

    /**
     * 导入命令
     * @param command 待导入的命令
     */
    @MessageDsl
    fun command(command: BotCommand) {
        this.commands.add(command)
    }

    /**
     * 加载命令
     */
    fun install(loader: CommandMap.() -> Unit) {
        loader(this)
    }

    fun getCommand(message: String): BotCommand {
        return getOrDefault(message.split(" ")[0], emptyCommand)
    }

    init {
        builder(this)
    }

}

/**
 * 空命令
 */
val emptyCommand = command("", "") {}