package com.poicraft.bot.v4.plugin.plugins

import net.mamoe.mirai.event.GroupMessageSubscribersBuilder

/**
 * 如何写一个命令
 *
 * 首先, 你需要在 plugins 包下新建一个 Kotlin 文件, 其名称应根据你需要实现的一个或一组命令的功能来定. 为了阐述方便, 此处采用 `abc` 这个名称.
 *
 * 之后, 在 abc.kt 中定义一个扩展函数, 这个函数的名称应与文件的名称相同, 被扩展的对象为 B:
 *
 * ```
 * fun B.abd(){
 *     //xxx
 * }
 * ```
 *
 * 在此函数内, 你可以使用 `command` 来编写命令, 具体方法见 [com.poicraft.bot.v4.plugin.command].
 *
 * 你也可以使用 `mirai` 提供的 DSL 语法来实现命令. **注意,** 此方法需手动实现鉴权和无关群排除. 使用方法见 [net.mamoe.mirai.event.subscribeMessages]
 *
 * 在编写玩命令后, 至 `PluginMain` 激活命令:
 *
 * ```
 * //...
 * GlobalEventChannel.subscribeGroupMessages {
 * //...
 *
 * abc()
 *
 * //...
 * }
 * //...
 * ```
 */
typealias B = GroupMessageSubscribersBuilder