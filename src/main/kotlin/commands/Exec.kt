package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import com.poicraft.bot.v4.plugin.constants.Permission
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote

/**
 * 执行命令
 * @see Command
 * @author topjohncian
 */
object Exec : Command() {
    /**
     * 命令的人类友好名称
     */
    override val name: String = "执行命令"

    override val introduction: String = "执行命令"

    override val permissionLevel: Permission = Permission.PERMISSION_LEVEL_ADMIN

    /**
     * 命令的程序友好名称
     */
    override val aliases: List<String> = listOf("exec")

    override val argsRequired: Int = 1

    @ExperimentalCoroutinesApi
    override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
        val result = BDXWSControl.runCmd(args[1])

        event.subject.sendMessage(event.source.quote() + result)
    }

}
