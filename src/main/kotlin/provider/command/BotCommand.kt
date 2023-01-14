@file:Suppress("unused")

package com.poicraft.bot.v4.plugin.provider.command

import com.poicraft.bot.v4.plugin.PluginData
import net.mamoe.mirai.event.GroupMessageSubscribersBuilder
import net.mamoe.mirai.event.events.GroupMessageEvent


typealias B = GroupMessageSubscribersBuilder

/**
 * 新的 Command 构造器
 * @author gggxbbb
 * @since 2021-08-11
 * @param name 命令的人类友好名称
 * @param aliases 命令的程序友好名称
 */
@Suppress("unused")
open class BotCommand(val name: String, val aliases: List<String>) {

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
    open suspend fun run(event: GroupMessageEvent, args: List<String>) {
        messageHandler(event, args)
    }

}

class BotOPCommand(name: String, aliases: List<String>) : BotCommand(name, aliases) {
    override suspend fun run(event: GroupMessageEvent, args: List<String>) {
        if (PluginData.adminGroup == event.group.id) {
            super.run(event, args)
        }
    }
}