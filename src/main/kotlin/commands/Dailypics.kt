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

/**
 * 图鉴日图
 * @author gggxbbb
 * @see Command
 */
object Dailypics : Command() {

    @Serializable
    data class TujianPic(val p_title: String, val PID: String, val username: String, val nativePath: String)

    override val name: String = "随机图鉴日图"
    override val aliases: List<String> = listOf(
        "dpic"
    )
    override val introduction: String = """随机获得一张图片
        |感谢图鉴日图Project https://dailypics.cn""".trimMargin()

    override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
        val data = Http.client()?.get<List<TujianPic>>("https://v2.api.dailypics.cn/random")
        val img = Http.client()?.get<InputStream>("https://s1.images.dailypics.cn${data?.get(0)?.nativePath}!w720")
        if (img != null) {
            data?.get(0)?.let {
                event.subject.sendMessage(
                    event.source.quote() +
                        PlainText(
                            """${it.p_title}
                            |via@${it.username}
                            |查看详情及原图: https://dailypics.cn/member/${it.PID}
                            """.trimMargin()
                        ) +
                        img.uploadAsImage(event.subject)
                )
            }
        }


    }
}