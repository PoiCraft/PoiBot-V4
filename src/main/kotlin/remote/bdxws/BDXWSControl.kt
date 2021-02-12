package com.poicraft.bot.v4.plugin.remote.bdxws

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.remote.Control
import com.poicraft.bot.v4.plugin.remote.bdxws.data.*
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.utils.info
import kotlin.system.exitProcess

object BDXWSControl : Control() {
    private val client = HttpClient {
        install(WebSockets)
    }

    private var retryTime = 0

    private lateinit var session: ClientWebSocketSession

    private lateinit var decryptUtil: BDXWSAESUtil

    /**
     * 初始化 Control
     */
    override fun init() {
        PluginMain.launch {
            try {
                val remoteConfig = PluginData.remoteConfig
                session = client.webSocketRawSession(
                    HttpMethod.Get,
                    remoteConfig.host,
                    remoteConfig.port,
                    remoteConfig.path
                )

                decryptUtil = BDXWSAESUtil(remoteConfig.password)
            } catch (e: Exception) {
                e.printStackTrace()
                retryTime++
                if (retryTime < 5) {
                    init()
                } else {
                    exitProcess(-1)
                }
            }

            while (true) {
                when (val frame = session.incoming.receive()) {
                    is Frame.Text -> decryptData(frame.readText())
                    else -> println(frame)
                }
            }
        }


    }

    private fun decryptData(data: String) {
        val rawJson = Json.decodeFromString<RawJson>(data)
        val rawString = decryptUtil.decrypt(rawJson.params.raw)
        PluginMain.logger.info { resJson.decodeFromString<RemoteResponse>(rawString).toString() }
        when (val res = resJson.decodeFromString<RemoteResponse>(rawString)) {
            is OnChatRes -> PluginMain.logger.info { res.params.text }
        }
    }

    /**
     * 执行命令(不需要获得返回值)
     */
    override suspend fun runCmdNoRes(cmdString: String) {
        val body = bodyJson.encodeToString(
            RunCmdRequestBody(
                RunCmdRequestParam(cmdString, 0)
            ) as RemoteBody
        )

        val rawString = rawJson.encodeToString(
            RawJson(
                RawJsonParams(
                    decryptUtil.encrypt(body)
                )
            )
        )
        PluginMain.logger.info(body)
        PluginMain.logger.info(rawString)
        session.send(Frame.Text(rawString))
    }

    /**
     * 执行命令
     */
    override fun runCmd(cmdString: String): String {
        return ""
    }
}