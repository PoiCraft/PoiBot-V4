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
    fun command(name: String, alias: String, builder: BotCommand.() -> Unit) {
        val cmd = BotCommand(name, listOf(alias))
        builder(cmd)
        this.commands.add(cmd)
    }

    /**
     * 导入命令
     * @param command 待导入的命令
     */
    fun command(command: BotCommand) {
        this.commands.add(command)
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

typealias B = GroupMessageSubscribersBuilder

fun <M : MessageEvent, Ret, R : RR, RR> MessageSubscribersBuilder<M, Ret, R, RR>.content(
    filter: M.(String) -> Boolean,
    onEvent: MessageListener<M, RR>
): Ret =
    subscriber(filter) { onEvent(this, it) }

fun GroupMessageEvent.containsCommand(): Boolean {
    var message = this.message.contentToString()
    if (message.startsWith("#")) {
        if (!PluginData.groupList.contains(source.group.id)) {
            return false
        }
        message = message.removePrefix("#")

        val args: List<String> = message.split(" ")

        return if (args.isNotEmpty()) commandMap.containsKey(args[0]) else false
    } else {
        return false
    }
}

@MessageDsl
fun B.commandImpl() = content({ containsCommand() }) {
    var message = this.message.contentToString()
    if (message.startsWith("#")) {
        if (!PluginData.groupList.contains(source.group.id)) {
            return@content
        }
        message = message.removePrefix("#")

        val args: List<String> = message.split(" ")

        commandMap.getCommand(message).run(this, args)
    }
}

/**
 * 构造命令
 * @param name 命令的人类友好名称
 * @param aliases 命令的程序友好名称
 */
@MessageDsl
fun B.command(
    name: String, aliases: List<String>, builder: @MessageDsl BotCommand.() -> Unit
): Listener<GroupMessageEvent> {
    commandMap.command(name, aliases, builder)

    return commandImpl()
}

/**
 * 构造命令
 * @param name 命令的人类友好名称
 * @param alias 命令的程序友好名称
 */
@MessageDsl
fun B.command(
    name: String, alias: String, builder: @MessageDsl BotCommand.() -> Unit
): Listener<GroupMessageEvent> {
    commandMap.command(name, alias, builder)

    return commandImpl()
}