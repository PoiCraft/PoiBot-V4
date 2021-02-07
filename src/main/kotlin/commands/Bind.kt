@file:Suppress("DEPRECATION")

package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import com.poicraft.bot.v4.plugin.database.DatabaseManager
import com.poicraft.bot.v4.plugin.database.Users
import com.poicraft.bot.v4.plugin.utils.Status
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import org.ktorm.dsl.*
import java.time.Instant

object Bind : Command() {
    override val name: String = "绑定"

    override val aliases: List<String> = listOf(
        "bind"
    )

    override val introduction: String = """绑定 XboxID 与 QQ.
        |需提供参数 XboxID""".trimMargin()

    override val argsRequired: Int = 1

    override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
        if (DatabaseManager.instance().from(Users)
                .select(Users.QQNumber)
                .where {
                    ((Users.QQNumber eq event.sender.id.toString())
                        or (Users.XboxID eq args[1]))
                }.totalRecords == 0
        ) {
            DatabaseManager.instance().insert(Users) {
                set(it.XboxID, args[1])
                set(it.QQNumber, event.sender.id.toString())
                set(it.CreateTime, Instant.now().epochSecond.toString())
                set(it.Status, Status.NOT_VERIFIED.ordinal)
            }

            event.subject.sendMessage(event.source.quote()+"已绑定 ${args[1]}")
        } else {
            event.subject.sendMessage(event.source.quote()+"此XboxID或QQ号已被绑定")
        }
    }
}