package com.poicraft.bot.v4.plugin.plugins

import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.constants.Permission
import com.poicraft.bot.v4.plugin.constants.UserStatus
import com.poicraft.bot.v4.plugin.database.Users
import com.poicraft.bot.v4.plugin.dsl.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.orNull
import org.ktorm.dsl.*
import java.time.Instant

/**
 * 绑定
 */
@ExperimentalCoroutinesApi
fun B.bind() {
    /**
     * 绑定
     */
    command("绑定") by "bind" intro "#bind <XboxID> 以绑定 Xbox ID" run { event, args ->
        val xboxID = args.getOrNull(1)
        if (xboxID == null) {
            event.subject.sendMessage("请提供 Xbox ID")
        } else {
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
     * 解除绑定
     */
    command("解除绑定") by "unbind" require Permission.ADMIN run { event, args ->
        val at: At? by event.message.orNull()
        if (at != null) {
            PluginMain.database.delete(Users) { it.qqNumber eq at!!.target }
            event.subject.sendMessage(event.source.quote() + "绑定已解除" + at!!.target.toString())
        } else {
            PluginMain.database.delete(Users) { it.xboxId eq args[1] }
            event.subject.sendMessage(event.source.quote() + "绑定已解除" + args[1])
        }
    }

    /**
     * 列出已绑定的玩家
     */
    command("列出已绑定玩家") by "lsbind" require Permission.ADMIN run { event, args ->
        val targets = PluginMain.database.from(Users).select(Users.qqNumber, Users.xboxId)
            .map { TargetUser(it.getLong(1), it.getString(2)!!) }
        var msg = "已绑定的玩家:\nQQ   Xbox"
        targets.forEach {
            msg += "\n ${it.QQNumber} ${it.XboxID}"
        }
        event.subject.sendMessage(msg)
    }
}

data class TargetUser(val QQNumber: Long, val XboxID: String)