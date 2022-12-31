package com.poicraft.bot.v4.plugin.functions.services

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.data.http.AlertManagerWebhookData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.toPlainText

@ExperimentalCoroutinesApi
fun Routing.alertManagerRoute() {
    post(PluginData.httpConfig.alertManagerWebhook) {
        try {
            val data = call.receive<AlertManagerWebhookData>()
            val bot = Bot.instances.last()

            val message = data.run {
                """
                Bot 告警:
                Summary: 
                [${if (status === "resolved") "已修复" else status}] - [${commonLabels.severity}] ${commonLabels.alertname} ${commonLabels.instance}
                ${commonAnnotations.summary}
                """.trimIndent()
            }

            val forward = buildForwardMessage(bot.asFriend) {
                data.alerts.forEach {
                    add(bot, it.run {
                        """
                        Summary: [${if (status === "resolved") "已修复" else status}] ${annotations.summary}
                        
                        Description: ${annotations.description}
                        
                        StartsAt: $startsAt
                        EndsAt: $endsAt
                        
                        Links: $generatorURL
                        """.trimIndent()
                    }.toPlainText())
                }
            }

            bot.getGroup(PluginData.adminGroup)?.apply {
                sendMessage(message)
                sendMessage(forward)
            }

            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            PluginMain.logger.error(e)
        }

    }
}