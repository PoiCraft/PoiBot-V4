package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import com.poicraft.bot.v4.plugin.request.Http
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.InputStream

object Dailypics : Command() {

    @Serializable
    data class TujianPic(val p_title: String, val PID: String, val username: String, val nativePath: String)

    override val name: String = "图鉴"
    override val aliases: List<String> = listOf(
        "dpic"
    )
    override val introduction: String ="随机获得一张图片\n感谢 图鉴日图Project https://dailypics.cn"

    override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
        val data = Http.client()?.get<List<TujianPic>>("https://v2.api.dailypics.cn/random")
        val img = Http.client()?.get<InputStream>("https://s1.images.dailypics.cn${data?.get(0)?.nativePath}")
        if (img != null) {
            data?.get(0)?.let {
                event.subject.sendMessage(
                    event.source.quote() +
                        PlainText("${data[0].p_title}\nvia@${data[0].username}\n") +
                        img.uploadAsImage(event.subject) +
                        PlainText("\n查看详情: https://dailypics.cn/member/${data[0].PID}")
                )
            }
        }


    }
}