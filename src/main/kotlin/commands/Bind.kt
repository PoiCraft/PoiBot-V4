package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.constants.Permission
import com.poicraft.bot.v4.plugin.constants.UserStatus
import com.poicraft.bot.v4.plugin.database.Users
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.orNull
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
        |#bind set <XboxID> 以绑定 Xbox ID
        |#bind del @某人 以解除某人的绑定""".trimMargin()

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
            val xboxID = args[1]
            if (PluginMain.database.from(Users) /*判断是否已被绑定*/
                    .select(Users.qqNumber)
                    .where {
                        ((Users.qqNumber eq event.sender.id)
                            or (Users.xboxId eq xboxID))
                    }.totalRecords == 0
            ) {
                PluginMain.database.insert(Users) { /*实现绑定*/
                    set(it.xboxId, xboxID)
                    set(it.qqNumber, event.sender.id)
                    set(it.createTime, Instant.now().epochSecond.toString()) /*绑定 时间戳*/
                    set(it.status, UserStatus.NOT_VERIFIED.ordinal) /*默认验证未通过 status=0*/
                }

                event.subject.sendMessage(
                    event.source.quote() + """已绑定 $xboxID
                    |但您还需要进服在聊天框发送 #bind ${event.sender.id} 进行验证
                """.trimMargin()
                )
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
        override val name: String = "解除绑定"
        override val argsRequired: Int = 1
        override val aliases: List<String> = listOf(
            "del"
        )
        override val permissionLevel: Permission = Permission.PERMISSION_LEVEL_ADMIN /*需群主或管理员*/
        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            val at: At? by event.message.orNull()
            if (at != null) {
                PluginMain.database.delete(Users) { it.qqNumber eq at!!.target }
                event.subject.sendMessage(event.source.quote() + "绑定已解除" + at!!.target.toString())
            } else {
                PluginMain.database.delete(Users) { it.xboxId eq args[1] }
                event.subject.sendMessage(event.source.quote() + "绑定已解除" + args[1])
            }
        }
    }

    object ListPlayers : Command() {

        data class TargetUser(val QQNumber: Long, val XboxID: String)

        override val name: String = "列出玩家"
        override val aliases: List<String> = listOf(
            "ls"
        )
        override val permissionLevel: Permission = Permission.PERMISSION_LEVEL_ADMIN
        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            val targets = PluginMain.database.from(Users).select(Users.qqNumber, Users.xboxId)
                .map { TargetUser(it.getLong(1), it.getString(2)!!) }
            var msg = "已绑定的玩家:\nQQ   Xbox"
            targets.forEach {
                msg += "\n ${it.QQNumber} ${it.XboxID}"
            }
            event.subject.sendMessage(msg)
        }
    }

    init { /*注册子命令*/
        newSubCommand(Add)
        newSubCommand(Remove)
        newSubCommand(ListPlayers)
    }


}