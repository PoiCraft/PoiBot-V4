@file:Suppress("DEPRECATION")

package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import com.poicraft.bot.v4.plugin.database.DatabaseManager
import com.poicraft.bot.v4.plugin.database.Users
import com.poicraft.bot.v4.plugin.utils.Status
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.ktorm.dsl.insert
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
        DatabaseManager.instance().insert(Users) {
            set(it.XboxID, args[1])
            set(it.QQNumber, event.sender.id.toString())
            set(it.CreateTime, Instant.now().epochSecond.toString())
            set(it.Status, Status.NOT_VERIFIED.ordinal)
        }

        event.subject.sendMessage("已绑定 ${args[1]}")
    }
}