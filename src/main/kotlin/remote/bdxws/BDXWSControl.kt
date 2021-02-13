package com.poicraft.bot.v4.plugin.remote.bdxws

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.remote.Control
import com.poicraft.bot.v4.plugin.remote.bdxws.data.*
import io.ktor.client.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.system.exitProcess

object BDXWSControl : Control() {
    private val client = HttpClient {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    PluginMain.logger.info(message)
                }
            }
        }

        WebSockets {
            pingInterval = 1000L
        }
    }

    private var retryTime = 0

    private lateinit var session: ClientWebSocketSession

    private lateinit var decryptUtil: BDXWSAESUtil

    private val cmdFeedbackChannels = mutableMapOf(0 to Channel<String>())

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

    private suspend fun decryptData(data: String) {
        val rawJson = Json.decodeFromString<RawJson>(data)
        val rawString = decryptUtil.decrypt(rawJson.params.raw)
        when (val res = resJson.decodeFromString<RemoteResponse>(rawString)) {
            is OnRunCmdFeedbackRes -> {
                val channel = cmdFeedbackChannels[res.params.id]
                channel?.send(res.params.result)
                channel?.close()
            }
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
        session.send(rawString)
    }

    /**
     * 执行命令
     */
    override suspend fun runCmd(cmdString: String): String {
        val cmdID = (1..10000).random()

        val body = bodyJson.encodeToString(
            RunCmdRequestBody(
                RunCmdRequestParam(cmdString, cmdID)
            ) as RemoteBody
        )

        val rawString = rawJson.encodeToString(
            RawJson(
                RawJsonParams(
                    decryptUtil.encrypt(body)
                )
            )
        )
        val channel = Channel<String>()
        session.send(rawString)
        cmdFeedbackChannels[cmdID] = channel
        return channel.receive()
    }
}