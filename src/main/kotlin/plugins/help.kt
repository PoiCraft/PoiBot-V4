package com.poicraft.bot.v4.plugin.plugins

import com.poicraft.bot.v4.plugin.CommandBox
import com.poicraft.bot.v4.plugin.getCommandNameAndArgs
import com.poicraft.bot.v4.plugin.utils.getSimilarCommands

/**
 * 机器人帮助
 */
fun B.help() {
    /**
     * 命令名纠错
     */
    subscriber({
        val name = getCommandNameAndArgs().first
        (!CommandBox.keys.contains(name)) and (name != "")
    }) {
        val (_, args) = getCommandNameAndArgs()
        val similarOnes = getSimilarCommands(args.first())
        var opt = "未知命令 " + args.first()
        if (similarOnes.isNotEmpty()) {
            opt += "\n你可能想使用: "
            opt += similarOnes.joinToString(", ")
        }
        this.subject.sendMessage(opt)
    }
}