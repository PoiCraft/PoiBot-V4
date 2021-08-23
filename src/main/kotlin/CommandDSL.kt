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
    private var permissionLevel = Permission.EVERYONE

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
            Permission.EVERYONE -> true
            Permission.ADMIN -> event.sender.isOperator()
            Permission.OWNER -> event.sender.isOwner()
            Permission.ADMIN_GROUP -> event.group.id == PluginData.adminGroup
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


    /**
     * 运行命令
     */
    suspend fun run(event: GroupMessageEvent, args: List<String>) {
        if (checkPermission(event))  /* 鉴权 */
            messageHandler(event, args)
        else
            permissionDeniedHandler(this.permissionLevel, event, args)
    }

}