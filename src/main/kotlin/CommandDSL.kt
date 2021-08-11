@file:Suppress("unused")

package com.poicraft.bot.v4.plugin

import com.poicraft.bot.v4.plugin.constants.Permission
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.contact.isOwner
import net.mamoe.mirai.event.events.GroupMessageEvent

/**
 * 新的 Command 构造器
 * @author gggxbbb
 * @since 2021-08-11
 * @param name 命令的人类友好名称
 * @param aliases 命令的程序友好名称
 */
@Suppress("unused")
class BotCommand(val name: String, val aliases: List<String>) {

    /* 基础常量 */
    /**
     * 介绍
     */
    var introduction: String = ""

    /**
     * 为命令设置介绍
     */
    fun intro(introduction: String) {
        this.introduction = introduction
    }
    /* end 基础常量 */

    /* 权限 */
    /**
     * 权限等级
     */
    private var permissionLevel = Permission.PERMISSION_LEVEL_EVERYONE

    /**
     * 设置权限等级
     */
    fun require(level: Permission) {
        this.permissionLevel = level
    }

    /**
     * 权限异常处理器, 默认啥也不做
     */
    private var permissionDeniedHandler: suspend (permissionLevel: Permission, event: GroupMessageEvent, args: List<String>) -> Unit =
        { _: Permission, _: GroupMessageEvent, _: List<String> -> }

    /**
     * 设置权限异常处理器
     */
    fun onPermissionDenied(permissionDeniedHandler: suspend (permissionLevel: Permission, event: GroupMessageEvent, args: List<String>) -> Unit) {
        this.permissionDeniedHandler = permissionDeniedHandler
    }

    /**
     * 鉴权
     */
    private fun checkPermission(event: GroupMessageEvent): Boolean {
        return when (this.permissionLevel) {
            Permission.PERMISSION_LEVEL_EVERYONE -> true
            Permission.PERMISSION_LEVEL_ADMIN -> event.sender.isOperator()
            Permission.PERMISSION_LEVEL_OWNER -> event.sender.isOwner()
        }
    }
    /* end 权限 */

    /* 消息处理器器 */
    /**
     * 消息处理器, 默认啥也不做
     */
    private var messageHandler: suspend (GroupMessageEvent, List<String>) -> Unit =
        { _: GroupMessageEvent, _: List<String> -> }

    /**
     * 设置消息处理器
     */
    fun onMessage(messageHandler: suspend (GroupMessageEvent, List<String>) -> Unit) {
        this.messageHandler = messageHandler
    }

    /* 子命令 */
    /**
     * 存储 subCommands 所用的 Map
     */
    private var subCommands: MutableMap<String, BotCommand> = mutableMapOf()

    /**
     * 注册新的 subCommand, 调用则激活子命令
     */
    fun command(name: String, aliases: List<String>, builder: BotCommand.() -> Unit) {
        val cmd = BotCommand(name, aliases)
        builder(cmd)
        for (com in cmd.aliases) {
            subCommands[com] = cmd
        }
    }
    /* end 子命令 */

    /* 向下兼容 */
    var proxy: Command? = null
    fun cmdProxy(cmd: Command) {
        this.proxy = cmd
    }
    /* end 向下兼容 */

    /**
     * 运行命令
     */
    suspend fun run(event: GroupMessageEvent, args: List<String>) {
        if (proxy != null) { /* 向下兼容 */
            proxy?.onMessage(event, args)
            return
        }
        if (!checkPermission(event)) return /* 鉴权 */
        if (this.subCommands.isEmpty()) { /*未启用子命令 */
            messageHandler(event, args)
        } else { /* 启用子命令 */
            if (args.isEmpty()) { /* 没参数 */
                messageHandler(event, args)
                return
            } else {
                if (subCommands.keys.contains(args.getOrElse(1) { "" })) {
                    subCommands[args[1]]!!.run(event, args.subList(1, args.size))
                } else {
                    messageHandler(event, args)
                }
            }
        }
    }

}

fun command(name: String, aliases: List<String>, builder: BotCommand.() -> Unit): BotCommand {
    val cmd = BotCommand(name, aliases)
    builder(cmd)
    return cmd
}

fun updateCommand(cmd: Command): BotCommand = command(cmd.name, cmd.aliases) { cmdProxy(cmd) }

fun Command.update() = command(this.name, this.aliases) { cmdProxy(this@update) }