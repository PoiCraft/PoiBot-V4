@file:Suppress("DEPRECATION")

package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import com.poicraft.bot.v4.plugin.database.DatabaseManager
import com.poicraft.bot.v4.plugin.database.Users
import com.poicraft.bot.v4.plugin.utils.Permission
import com.poicraft.bot.v4.plugin.utils.Status
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import org.ktorm.dsl.*
import java.time.Instant

/**
 * QQ 与 Xbox ID 绑定管理
 * @author gggxbbb
 * @see Command
 */
object Bind : Command() {
    override val name: String = "绑定"

    override val aliases: List<String> = listOf(
        "bind"
    )

    override val introduction: String = """管理 QQ 与 Xbox ID 的绑定关系
        |#bind set <XboxID> 以绑定 Xbox ID""".trimMargin()

    override val enableSubCommand: Boolean = true /*启用子命令支持*/

    /**
     * 实现绑定
     * #bind set <XboxID>
     * @see Command
     */
    object Add : Command() {
        override val name: String = "新绑定"
        override val argsRequired: Int = 1
        override val aliases: List<String> = listOf(
            "set"
        )

        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            if (DatabaseManager.instance().from(Users) /*判断是否已被绑定*/
                    .select(Users.QQNumber)
                    .where {
                        ((Users.QQNumber eq event.sender.id.toString())
                                or (Users.XboxID eq args[1]))
                    }.totalRecords == 0
            ) {
                DatabaseManager.instance().insert(Users) { /*实现绑定*/
                    set(it.XboxID, args[1])
                    set(it.QQNumber, event.sender.id.toString())
                    set(it.CreateTime, Instant.now().epochSecond.toString()) /*绑定 时间戳*/
                    set(it.Status, Status.NOT_VERIFIED.ordinal) /*默认验证未通过 status=0*/
                }

                event.subject.sendMessage(event.source.quote() + "已绑定 ${args[1]}")
            } else {
                event.subject.sendMessage(event.source.quote() + "此XboxID或QQ号已被绑定")
            }
        }
    }

    /**
     * 实现解绑
     * @see Command
     */
    object Remove : Command() {
        override val name: String = "消绑定"
        override val argsRequired: Int = 1
        override val aliases: List<String> = listOf(
            "del"
        )
        override val permissionLevel: Permission = Permission.PERMISSION_LEVEL_ADMIN /*需群主或管理员*/
        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            val target = args[1]
                .replace("[mirai:at:", "")
                .replace("]", "")
                .replace("@", "")
            DatabaseManager.instance().delete(Users) { it.QQNumber eq target }
            event.subject.sendMessage(event.source.quote() + "绑定已解除" + target)
        }
    }

    init { /*注册子命令*/
        newSubCommand(Add)
        newSubCommand(Remove)
    }


}