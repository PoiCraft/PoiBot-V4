package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.utils.Permission
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.contact.isOwner
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote

/**
 * 所有命令的父类
 * */
@Suppress("ImplicitNullableNothingType")
abstract class Command {
    /**
     * 命令的人类友好名称
     */
    abstract val name: String

    /**
     * 命令的程序友好名称
     */
    abstract val aliases: List<String>

    /**
     * 命令的简介 (可选)
     */
    open val introduction: String = ""

    /**
     * 命令的所需的权限等级, 默认为 任意群成员
     * @see Permission
     */
    open val permissionLevel = Permission.PERMISSION_LEVEL_EVERYONE

    /**
     * 命令所执行的事件
     */
    open suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {}

    private var subCommands: MutableMap<String, Command> = mutableMapOf()

    fun newSubCommand(command: Command) {
        for (com in command.aliases) {
            subCommands[com] = command
        }
    }

    open val enableSubCommand: Boolean = false

    private class Helper(val f_name: String, val f_subCommands: Map<String, Command>) : Command() {
        override val name: String = "帮助"
        override val aliases: List<String> = listOf()
        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            var msg = "未知的子命令${args[0]}\n $f_name 支持以下子命令"
            for (cmd in f_subCommands) {
                msg += "\n${cmd.key} ${cmd.value.name}"
            }
            event.subject.sendMessage(msg.trimIndent())
        }

    }

    /**
     * 权限等级不足时执行的事件 (可选) 默认为空
     */
    open suspend fun onPermissionDenied(permissionLevel: Permission, event: GroupMessageEvent, args: List<String>) {
        event.subject.sendMessage(event.source.quote() + "权限不足")
    }

    open val argsRequired: Int = 0
    open val unlimitedArgs: Boolean = false

    open suspend fun onArgsMissing(argsRequired: Int, event: GroupMessageEvent, args: List<String>) {
        event.subject.sendMessage(
            event.source.quote() + """
            提供的参数( ${args.size - 1} 个)数量异常,需要 $argsRequired 个
            ------
            $introduction
            """.trimIndent()
        )
    }

    /**
     * 鉴权
     */
    open suspend fun onMessage(event: GroupMessageEvent, args: List<String>) {

        val helper = Helper(name, subCommands)

        if (((args.size - 1) != argsRequired) and !unlimitedArgs and !enableSubCommand) {
            onArgsMissing(argsRequired, event, args)
            return
        }
        when (permissionLevel) {
            Permission.PERMISSION_LEVEL_EVERYONE ->
                if (!enableSubCommand or (args.size == 1)) handleMessage(
                    event,
                    args
                ) else subCommands.getOrDefault(args[1], helper).onMessage(
                    event,
                    args.subList(1, args.size)
                )

            Permission.PERMISSION_LEVEL_ADMIN ->
                if (event.sender.isOperator())
                    if (!enableSubCommand or (args.size == 1)) handleMessage(
                        event,
                        args
                    ) else subCommands.getOrDefault(args[1], helper).onMessage(
                        event,
                        args.subList(1, args.size)
                    )
                else
                    onPermissionDenied(Permission.PERMISSION_LEVEL_ADMIN, event, args)

            Permission.PERMISSION_LEVEL_OWNER ->
                if (event.sender.isOwner())
                    if (!enableSubCommand or (args.size == 1)) handleMessage(
                        event,
                        args
                    ) else subCommands.getOrDefault(args[1], helper).onMessage(
                        event,
                        args.subList(1, args.size)
                    )
                else
                    onPermissionDenied(Permission.PERMISSION_LEVEL_OWNER, event, args)
        }
    }
}