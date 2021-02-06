package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import com.poicraft.bot.v4.plugin.request.Http
import com.poicraft.bot.v4.plugin.utils.Permission
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote

object Hitokoto : Command() {
    @Serializable
    data class HitokotoData(val hitokoto: String, val from: String)

    override val name: String = "一言"
    override val aliases: List<String> = listOf(
        "Hitokoto",
        "yy",
        "一言"
    )
    override val introduction: String = "从网络获取一言"

    override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
        val data = Http.client()?.get<HitokotoData>("https://v1.hitokoto.cn/?c=a")
        event.subject.sendMessage(
            event.source.quote() + """
            ${data?.hitokoto}
            From: ${data?.from}
        """.trimIndent()
        )
    }
}