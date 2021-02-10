package com.poicraft.bot.v4.plugin.remote.bdxws

import com.poicraft.bot.v4.plugin.PluginData
import com.poicraft.bot.v4.plugin.PluginMain
import com.poicraft.bot.v4.plugin.remote.Control
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

object BDXWSControl : Control() {
    private val client = HttpClient {
        install(WebSockets)
    }

    private var retryTime = 0

    private lateinit var session: ClientWebSocketSession

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
                    is Frame.Text -> PluginMain.logger.info(frame.readText())
                    else -> println(frame)
                }
            }
        }


    }

    /**
     * 执行命令(不需要获得返回值)
     */
    override fun runCmdNoRes(cmdString: String) {
        TODO("Not yet implemented")
    }

    /**
     * 执行命令
     */
    override fun runCmd(cmdString: String): String {
        TODO("Not yet implemented")
    }
}