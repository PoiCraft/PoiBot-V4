@file:Suppress("unused")

package com.poicraft.bot.v4.plugin

import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent

/**
 * 命令表
 * @author topjohncian, gggxbbb
 */
object PluginBox : HashMap<String, BotCommand>() {

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

    fun getCommand(message: String): BotCommand {
        return getOrDefault(message.split(" ")[0], emptyCommand)
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

fun GroupMessageEvent.getCommandNameAndArgs(): Pair<String, List<String>> {
    var message = this.message.contentToString()
    if (message.startsWith("#")) {
        if (!PluginData.groupList.contains(source.group.id)) {
            return "" to listOf()
        }
        message = message.removePrefix("#")

        val args: List<String> = message.split(" ")

        return if (args.isNotEmpty())
            Pair(args[0], args)
        else Pair("", listOf())
    } else {
        return "" to listOf()
    }
}

@MessageDsl
fun B.commandImpl(aliases: List<String>) = content({ aliases.contains(getCommandNameAndArgs().first) }) {
    val (cmdName, args) = getCommandNameAndArgs()
    PluginBox.getCommand(cmdName).run(this, args)
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
    PluginBox.command(name, aliases, builder)

    return commandImpl(aliases)
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
    PluginBox.command(name, alias, builder)

    return commandImpl(listOf(alias))
}

class CommandNameHeader(val b: GroupMessageSubscribersBuilder, val name: String)
class CommandHeader(val b: GroupMessageSubscribersBuilder, val name: String, val aliases: List<String>)

/**
 * 构造命令
 * @param aliases 命令的程序友好名称
 */
infix fun CommandNameHeader.by(aliases: List<String>) = CommandHeader(this.b, this.name, aliases)

/**
 * 构造命令
 * @param alias 命令的程序友好名称
 */
infix fun CommandNameHeader.by(alias: String) = CommandHeader(this.b, this.name, listOf(alias))

/**
 * 构造命令
 * @param name 命令的人类友好名称
 */
@MessageDsl
infix fun B.command(name: String) = CommandNameHeader(this, name)

/**
 * 构造命令
 */
infix fun CommandHeader.run(builder: BotCommand.() -> Unit): Listener<GroupMessageEvent> {
    PluginBox.command(this.name, this.aliases, builder)
    return this.b.commandImpl(this.aliases)
}