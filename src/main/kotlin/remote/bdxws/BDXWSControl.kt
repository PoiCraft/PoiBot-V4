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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass
import kotlin.system.exitProcess

typealias EventHandler<E> = suspend E.(E) -> Unit

class ListenerRegistry(
    val listener: EventHandler<RemoteResponse>,
    val type: KClass<out RemoteResponse>
)

@ExperimentalCoroutinesApi
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

    private val eventListeners = mutableListOf<ListenerRegistry>()

    private var crashCallback = { _: Throwable -> }

    fun onCrash(callback: (Throwable) -> Unit) {
        crashCallback = callback
    }

    /**
     * 初始化 Control
     */
    override fun init() {
        PluginMain.launch {
            try {
                val remoteConfig = PluginData.remoteConfig
                session = client.webSocketSession(
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
                    crashCallback(e)
                    exitProcess(-1)
                }
            }

            while (true) {
                try {
                    when (val frame = session.incoming.receive()) {
                        is Frame.Text -> PluginMain.launch { decryptData(frame.readText()) }
                        is Frame.Pong -> println(frame)
                        is Frame.Ping -> println(frame)
                        else -> println(frame)
                    }
                } catch (e: ClosedReceiveChannelException) {
                    init()
                } catch (e: Throwable) {
                    e.printStackTrace()
                    retryTime++
                    if (retryTime < 5) {
                        init()
                    } else {
                        crashCallback(e)
                        exitProcess(-1)
                    }
                }
            }
        }


    }

    private suspend fun decryptData(data: String) {
        val rawJson = Json.decodeFromString<RawJson>(data)
        val rawString = decryptUtil.decrypt(rawJson.params.raw)
        val res = resJson.decodeFromString<RemoteResponse>(rawString)
        when (res) {
            is OnRunCmdFeedbackRes -> {
                val channel = cmdFeedbackChannels[res.params.id]
                channel?.send(res.params.result)
                if (res.params.id != 0) {
                    channel?.close()
                    cmdFeedbackChannels.remove(res.params.id)
                }
            }
            else -> {
            }
        }
        eventListeners.forEach {
            if (res::class === it.type) it.listener(res, res)
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

    /**
     * 添加一个事件监听器
     */
    @Suppress("UNCHECKED_CAST")
    fun <E : RemoteResponse> addEventListener(eventClass: KClass<out E>, handler: EventHandler<E>) {
        eventListeners.add(ListenerRegistry(handler as EventHandler<RemoteResponse>, eventClass))
    }

    inline fun <reified E : RemoteResponse> addEventListener(noinline handler: EventHandler<E>) {
        addEventListener(E::class, handler)
    }

}
