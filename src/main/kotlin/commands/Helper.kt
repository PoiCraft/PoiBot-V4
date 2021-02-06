package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import net.mamoe.mirai.event.events.GroupMessageEvent

/**
 * Bot帮助
 * @author gggxbbb
 * @see Command
 */
object Helper : Command() {

    private val commandMap = hashMapOf<String, Command>()

    fun load(command_list: List<Command>) {
        for (v in command_list) {
            commandMap[v.name] = v
        }
    }

    override val name: String = "帮助"

    override val aliases: List<String> = listOf(
        "help",
        "帮助"
    )
    override val introduction: String = "查看帮助"
    override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
        var msg = ""
        if (args.singleOrNull() == null) {
            val command = commandMap.getOrDefault(args[1], null)
            if (command != null) {
                msg += "命令:\n"
                for (v in command.aliases) {
                    msg += ("#$v\n")
                }
                msg += ("\n" + command.introduction)
            } else {
                msg += "找不到此命令"
            }
        } else {
            msg += "输入 #help <名称> 获得帮助:\n"
            for (v in commandMap.values) {
                msg += (v.name + "\n")
            }
        }

        event.subject.sendMessage(msg.trimIndent())
    }


}