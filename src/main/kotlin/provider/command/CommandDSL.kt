@file:Suppress("unused")

package com.poicraft.bot.v4.plugin.provider.command

import net.mamoe.mirai.event.GroupMessageSubscribersBuilder
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.MessageDsl
import net.mamoe.mirai.event.events.GroupMessageEvent


class CommandNameHeader(
    val b: GroupMessageSubscribersBuilder,
    val name: String,
    val op: Boolean = false,
)

class CommandHeader(
    val b: GroupMessageSubscribersBuilder,
    val name: String,
    val aliases: List<String>,
    val op: Boolean,
    var introduction: String = ""
)

/**
 * 构造命令
 * @param aliases 命令的程序友好名称
 */
infix fun CommandNameHeader.by(aliases: List<String>) = CommandHeader(this.b, this.name, aliases, this.op)

/**
 * 构造命令
 * @param alias 命令的程序友好名称
 */
infix fun CommandNameHeader.by(alias: String) = CommandHeader(this.b, this.name, listOf(alias), this.op)

/**
 * 构造命令
 *
 * # 用法
 *
 * ## 声明一个命令
 *
 * 声明一个命令是非常简单的, 只需要这样:
 * ```
 * command("测试命令")
 * ```
 * 这将新建了一个名为 *测试命令* 的命令, 但这一命令徒有其名, 不会被加载, 没有事件, 也永远不会被调用.
 *
 * ## 为命令分配命令
 *
 * 这听起来或许有些奇怪, 但你必须清楚: 之前所设定的 *测试命令* 这个名称并不是给机器看的, 我们需要告诉机器何时调用这个命令. 当然这也十分容易:
 * ```
 * command("测试命令") by "test"
 * ```
 * 这样, 当机器人收到 `#test` 时, 程序将会调用这个命令.
 *
 * 此外, 如果你需要设置别名, 可以这样:
 * ```
 * command("测试命令") by listOf("test", "t") //任意人都可以使用
 * commandOP("测试命令") by listOf("test", "t") //仅管理群内成员可以使用
 * ```
 * 这样, 当机器人收到 `#test` 或 `#t` 时, 程序将会调用这个命令.
 *
 * ## 更详细的命令配置
 *
 * 下述配置顺序任意, 可以共存. 默认以 command 为例.
 *
 * ### 命令简介
 *
 * 默认情况下, 命令简介为空. 如果你需要修改, 可以这样:
 * ```
 * command("测试命令") by "test" intro "这是命令简介"
 * ```
 *
 * ## 最终配置
 *
 * **注意**: 无最终配置的命令不会被注册
 *
 * 下述配置任选其一
 *
 * ### 简单发送 `reply`
 *
 * 这一配置适用于简单命令, 即返回固定内容的命令.
 *
 * 用法如下:
 * ```
 * command("测试命令") by "test" reply "测试通过"
 * ```
 *
 * 此外, reply 也支持复杂回复, 用法如下:
 * ```
 * command("测试命令") by "test" reply {
 *    val msg = "测试通过"
 *    msg // 返回值即为回复内容
 *    }
 * ```
 *
 * ### 普通事件 `run`
 *
 * 详细配置命令的事件
 *
 * 用法如下:
 * ```
 * command("测试命令") by "test" run { event, args ->
 *         // 执行内容
 * }
 *
 * ```
 * ### 完整配置 `to`
 *
 * 这是最完整配置方法.
 *
 * **注意**, 在 `to` 中设置的 `require` 和 `intro` 将覆盖之前的配置.
 *
 * 用法如下:
 * ```
 * command("测试命令") by "test" to {
 *     intro("命令简介")
 *     onMessage { event, args ->
 *         // 执行内容
 *     }
 * }
 * ```
 *
 * ----------------
 * 构造无特殊权限需求的命令
 * @param name 命令的人类友好名称
 */
@MessageDsl
infix fun B.command(name: String) = CommandNameHeader(this, name)

/**
 * 构造仅管理群内人员可使用的命令
 * @param name 命令的人类友好名称
 * @see command
 */
@MessageDsl
infix fun B.commandOP(name: String) = CommandNameHeader(this, name, true)

/**
 * 设置命令介绍, 非必须
 * @param introduction 命令介绍
 */
infix fun CommandHeader.intro(introduction: String): CommandHeader {
    this.introduction = introduction
    return this
}

/**
 * 命令完整构造
 */
infix fun CommandHeader.to(builder: BotCommand.() -> Unit): Listener<GroupMessageEvent> {
    val cmd = if (this.op) BotOPCommand(this.name, this.aliases) else BotCommand(this.name, this.aliases)
    cmd.intro(this.introduction)
    builder(cmd)
    CommandBox.registerCommand(cmd)
    return this.b.commandImpl(this.aliases)
}


/**
 * 构造命令
 */
infix fun CommandHeader.run(onMessage: suspend (GroupMessageEvent, List<String>) -> Unit): Listener<GroupMessageEvent> {
    val cmd = if (this.op) BotOPCommand(this.name, this.aliases) else BotCommand(this.name, this.aliases)
    cmd.intro(this.introduction)
    cmd.onMessage(onMessage)
    CommandBox.registerCommand(cmd)
    return this.b.commandImpl(this.aliases)
}

/**
 * 简单回复
 */
infix fun CommandHeader.reply(message: String): Listener<GroupMessageEvent> {
    val cmd = if (this.op) BotOPCommand(this.name, this.aliases) else BotCommand(this.name, this.aliases)
    cmd.intro(this.introduction)
    cmd.onMessage { event, _ -> event.subject.sendMessage(message) }
    CommandBox.registerCommand(cmd)
    return this.b.commandImpl(this.aliases)
}

/**
 * 复杂回复
 */
infix fun CommandHeader.reply(message: suspend () -> String): Listener<GroupMessageEvent> {
    val cmd = if (this.op) BotOPCommand(this.name, this.aliases) else BotCommand(this.name, this.aliases)
    cmd.intro(this.introduction)
    cmd.onMessage { event, _ -> event.subject.sendMessage(message()) }
    CommandBox.registerCommand(cmd)
    return this.b.commandImpl(this.aliases)
}

