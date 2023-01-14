package com.poicraft.bot.v4.plugin.functions.commands

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.provider.command.B
import com.poicraft.bot.v4.plugin.provider.command.Command
import com.poicraft.bot.v4.plugin.provider.command.*
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import com.poicraft.bot.v4.plugin.utils.minecraft.cleanMsg
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.message.data.MessageSource.Key.quote

/**
 * 执行任意命令, 仅限 adminGroup 使用
 */
@Command
@ExperimentalCoroutinesApi
fun B.exec() {

    /**
     * 实现 /xxx 直接执行命令
     */
    startsWith("/") reply { cmd ->
        if (PluginData.adminGroup == this.group.id /* 等价于 require Permission.ADMIN_GROUP */) {
            val result = BDXWSControl.runCmd(cmd).cleanMsg()
            this.subject.sendMessage(this.source.quote() + "执行完成:\n" + result)
        }
    }

    /**
     * 执行任意命令
     */
    commandOP("执行命令") by "exec" intro "执行命令" run { event, args ->
        val result = BDXWSControl.runCmd(args.subList(1, args.size).joinToString(" ")).cleanMsg()
        event.subject.sendMessage(event.source.quote() + "执行完成:\n" + result)
    }
}