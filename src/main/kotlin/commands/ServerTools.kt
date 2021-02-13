package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import com.poicraft.bot.v4.plugin.constants.Permission
import com.poicraft.bot.v4.plugin.database.ifOnline
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote

/**
 * 服务器管理类
 * @author gggxbbb
 * @see Command
 */
object ServerTools : Command() {
    override val name: String = "服务器工具"
    override val aliases: List<String> = listOf(
        "st"
    )
    override val permissionLevel: Permission = Permission.PERMISSION_LEVEL_ADMIN
    override val enableSubCommand: Boolean = true

    object ServerAnnounce : Command() {
        override val name: String = "全服公告"
        override val aliases: List<String> = listOf(
            "say"
        )

        override val unlimitedArgs: Boolean = true

        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            BDXWSControl.runCmdNoRes("say 消息来自于 QQ@ ${event.sender.nameCard}")
            args.subList(1, args.size).forEach {
                BDXWSControl.runCmdNoRes("say $it")
            }
            event.subject.sendMessage(event.source.quote() + "已发送")
        }
    }

    object KickPlayer : Command() {
        override val name: String = "踢出玩家"
        override val aliases: List<String> = listOf(
            "kick"
        )
        override val argsRequired: Int = 1

        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            BDXWSControl.runCmdNoRes("kick ${args[1]}")
            event.subject.sendMessage(event.source.quote() + "已执行")
        }
    }

    object KillPlayer : Command() {
        override val name: String = "杀死玩家"
        override val aliases: List<String> = listOf(
            "kill"
        )
        override val argsRequired: Int = 1

        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            val result = BDXWSControl.runCmd("kill ${args[1]}")
            event.subject.sendMessage(event.source.quote() + result)
        }
    }

    object IfOnline : Command() {
        override val name: String = "是否在线"
        override val aliases: List<String> = listOf(
            "on",
            "online"
        )
        override val argsRequired: Int = 1
        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            when (event.ifOnline(args[1])) {
                null -> event.subject.sendMessage(event.source.quote() + "未知玩家")
                true -> event.subject.sendMessage(event.source.quote() + "在线")
                false -> event.subject.sendMessage(event.source.quote() + "离线")
            }
        }
    }

    init {
        newSubCommand(ServerAnnounce)
        newSubCommand(KickPlayer)
        newSubCommand(KillPlayer)
        newSubCommand(IfOnline)
    }

}


/*
_______________#########_______________________
______________############_____________________
______________#############____________________
_____________##__###########___________________
____________###__######_#####__________________
____________###_#######___####_________________
___________###__##########_####________________
__________####__###########_####_______________
________#####___###########__#####_____________
_______######___###_########___#####___________
_______#####___###___########___######_________
______######___###__###########___######_______
_____######___####_##############__######______
____#######__#####################_#######_____
____#######__##############################____
___#######__######_#################_#######___
___#######__######_######_#########___######___
___#######____##__######___######_____######___
___#######________######____#####_____#####____
____######________#####_____#####_____####_____
_____#####________####______#####_____###______
______#####_______###________###______#________
________##_______####________####______________
*/
