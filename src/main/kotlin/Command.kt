package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.constants.Permission
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.contact.isOwner
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote

/**
 * 所有命令的父类
 */
@Deprecated("use BotCommand instead of Command")
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

    /**
     * 存储 subCommands 所用的 Map
     */
    private var subCommands: MutableMap<String, Command> = mutableMapOf()

    /**
     * 注册新的 subCommand
     */
    fun newSubCommand(command: Command) {
        for (com in command.aliases) {
            subCommands[com] = command
        }
    }

    /**
     * 是否支持子命令, 默认为否
     */
    open val enableSubCommand: Boolean = false

    /**
     * 为子命令实现帮助
     * @author gggxbbb
     */
    private class Helper(val f_name: String, val f_subCommands: Map<String, Command>) : Command() {
        override val name: String = "帮助"
        override val aliases: List<String> = listOf()
        override val unlimitedArgs: Boolean = true
        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            var msg = "未知的子命令${args.getOrNull(0)}\n $f_name 支持以下子命令"
            for (cmd in f_subCommands) {
                if (cmd.key == "_") {
                    continue
                }
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

    /**
     * 定义所需参数数量, 默认0个
     */
    open val argsRequired: Int = 0

    /**
     * 定义是否限制参数数量, 默认否
     * 当为 true 时忽略 argsRequired
     */
    open val unlimitedArgs: Boolean = false

    /**
     * 参数数量异常时调用
     */
    open suspend fun onArgsMissing(argsRequired: Int, event: GroupMessageEvent, args: List<String>) {
        event.subject.sendMessage(
            event.source.quote() + """|提供的参数( ${args.size - 1} 个)数量异常,需要 $argsRequired 个
            ${
                if (introduction != "") {
                    "|------\n$introduction"
                } else {
                    ""
                }
            }""".trimMargin()
        )
    }

    /**
     * 鉴权
     */
    open suspend fun onMessage(event: GroupMessageEvent, args: List<String>) {

        val helper = Helper(name, subCommands)

        when (permissionLevel) {
            Permission.PERMISSION_LEVEL_EVERYONE ->
                runIt(event, args, helper)

            Permission.PERMISSION_LEVEL_ADMIN ->
                if (event.sender.isOperator())
                    runIt(event, args, helper)
                else
                    onPermissionDenied(Permission.PERMISSION_LEVEL_ADMIN, event, args)

            Permission.PERMISSION_LEVEL_OWNER ->
                if (event.sender.isOwner())
                    runIt(event, args, helper)
                else
                    onPermissionDenied(Permission.PERMISSION_LEVEL_OWNER, event, args)
        }
    }

    private suspend fun runIt(event: GroupMessageEvent, args: List<String>, helper: Helper) {

        if (!enableSubCommand) { /*未启用子命令*/

            if (unlimitedArgs) { /*不限制参数数量*/

                handleMessage(event, args)

            } else { /*限制参数数量*/

                if (args.size - 1 == argsRequired) { /*参数数量符合条件*/

                    handleMessage(event, args)

                } else { /*参数数量不符合条件*/

                    onArgsMissing(argsRequired, event, args)

                }
            }

        } else { /*启用子命令*/

            if (subCommands.keys.contains("_")) { /*包含默认子命令*/

                if ((args.size >= 2) and subCommands.keys.contains(args.getOrElse(1) { "" })) { /*参数指向子命令*/

                    subCommands[args[1]]!!.onMessage( /*执行子命令*/
                        event,
                        args.subList(1, args.size)
                    )

                } else { /*参数未指向子命令*/

                    if (args.size - 1 == subCommands["_"]!!.argsRequired) { /*参数符合默认子命令参数数量条件*/

                        subCommands["_"]!!.onMessage(event, args)

                    } else { /*参数不符合默认子命令参数条件*/

                        helper.onMessage(event, args.subList(1, args.size)) /*视为指向未知子命令*/

                    }

                }

            } else { /*不包含默认子命令*/

                if (args.size == 1) { /*未指向子命令*/

                    helper.onMessage(event, args.subList(1, args.size)) /*视为指向未知子命令*/

                } else { /*指向子命令*/

                    subCommands.getOrDefault(args[1], helper).onMessage(
                        event,
                        args.subList(1, args.size)
                    )

                }


            }
        }

    }

}