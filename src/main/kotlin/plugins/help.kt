package com.poicraft.bot.v4.plugin.plugins

import com.poicraft.bot.v4.plugin.*
import com.poicraft.bot.v4.plugin.utils.getSimilarCommandNames
import com.poicraft.bot.v4.plugin.utils.getSimilarCommands
import com.poicraft.bot.v4.plugin.dsl.*

/**
 * 机器人帮助
 */
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
            var opt = "未知命令 " + args.first()
            opt += "\n你可能想使用: "
            opt += similarOnes.joinToString()
            this.subject.sendMessage(opt)
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
            var opt = "支持的功能有:\n"
            opt += names.keys.joinToString()
            event.subject.sendMessage(opt)
        } else {
            if (names.keys.contains(arg.first())) {
                val cmd = names[arg.first()]!!
                var opt = cmd.name
                opt += "\n命令: " + cmd.aliases.joinToString()
                if (cmd.introduction != "") {
                    opt += "\n" + cmd.introduction
                }
                event.subject.sendMessage(opt)
            } else { // 命令名纠错
                val similarOnes = getSimilarCommandNames(arg.first())
                var opt = "未知功能 " + arg.first()
                if (similarOnes.isNotEmpty()) {
                    opt += "\n你可能想查找: "
                    opt += similarOnes.joinToString()
                }
                event.subject.sendMessage(opt)
            }
        }

    }

}