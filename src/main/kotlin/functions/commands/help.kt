package com.poicraft.bot.v4.plugin.functions.commands

import com.poicraft.bot.v4.plugin.provider.command.BotCommand
import com.poicraft.bot.v4.plugin.provider.command.CommandBox
import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.provider.command.B
import com.poicraft.bot.v4.plugin.provider.command.Command
import com.poicraft.bot.v4.plugin.provider.command.by
import com.poicraft.bot.v4.plugin.provider.command.command
import com.poicraft.bot.v4.plugin.provider.command.run
import com.poicraft.bot.v4.plugin.provider.command.getCommandNameAndArgs
import com.poicraft.bot.v4.plugin.utils.getSimilarCommandNames
import com.poicraft.bot.v4.plugin.utils.getSimilarCommands

/**
 * 机器人帮助
 */
@Command
fun B.help() {
    /**
     * 命令名纠错
     */
    subscriber({
        val name = getCommandNameAndArgs().first
        (!CommandBox.keys.contains(name)) and (name != "") and PluginData.groupList.contains(this.group.id)
    }) {
        val (_, args) = getCommandNameAndArgs()
        val similarOnes = getSimilarCommands(args.first())
        if (similarOnes.isNotEmpty()) {
            this.subject.sendMessage("未知命令 ${args.first()}\n你可能想使用: ${similarOnes.joinToString()}")
        }
    }

    /**
     * 命令帮助
     */
    command("帮助") by "help" run { event, args ->
        val arg = args.subList(1, args.size)
        val names = mutableMapOf<String, BotCommand>()
        for (i in CommandBox.values) {
            names[i.name] = i
        }
        if (arg.isEmpty()) {
            event.subject.sendMessage("支持的功能有:\n ${names.keys.joinToString()}")
        } else {
            if (names.keys.contains(arg.first())) {
                val cmd = names[arg.first()]!!
                event.subject.sendMessage(
                    "${cmd.name}:\n命令: ${cmd.aliases.joinToString()}${
                        if (cmd.introduction != "") {
                            "\n" + cmd.introduction
                        } else {
                            ""
                        }
                    }"
                )
            } else { // 命令名纠错
                val similarOnes = getSimilarCommandNames(arg.first())
                event.subject.sendMessage(
                    "未知功能 ${arg.first()}${
                        if (similarOnes.isNotEmpty()) {
                            "\n你可能想查找: ${similarOnes.joinToString()}"
                        } else {
                            ""
                        }
                    }"
                )
            }
        }

    }

}