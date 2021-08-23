@file:Suppress("unused")

package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.constants.Permission
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent

/**
 * 命令表
 * @author topjohncian, gggxbbb
 */
object CommandBox : HashMap<String, BotCommand>() {

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
 * 不存在命令
 */
val emptyCommand = BotCommand("", listOf())

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
fun B.commandImpl(aliases: List<String>) =
    content({
        aliases.contains(getCommandNameAndArgs().first)
    }) {
        val (cmdName, args) = getCommandNameAndArgs()
        CommandBox.getCommand(cmdName).run(this, args)
    }

