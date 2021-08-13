package com.poicraft.bot.v4.plugin.plugins

import com.poicraft.bot.v4.plugin.by
import com.poicraft.bot.v4.plugin.run
import com.poicraft.bot.v4.plugin.command
import com.poicraft.bot.v4.plugin.constants.Permission
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.message.data.MessageSource.Key.quote

/**
 * 执行任意命令
 */
@ExperimentalCoroutinesApi
fun B.exec() {
    /**
     * 执行任意命令
     */
    command("执行命令") by "exec" run {
        intro("执行命令")
        require(Permission.PERMISSION_LEVEL_ADMIN)
        onMessage { event, args ->
            val result = BDXWSControl.runCmd(args.subList(1, args.size).joinToString(" "))
            event.subject.sendMessage(event.source.quote() + result)
        }
    }
}