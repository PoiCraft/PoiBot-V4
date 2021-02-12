package com.poicraft.bot.v4.plugin.remote.bdxws.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val bodyJson = Json {
    ignoreUnknownKeys = true
    classDiscriminator = "action"
    encodeDefaults = true
}

/**
 * 所有 Remote Body 的基类
 * https://github.com/WangYneos/BDXWebSocket
 * @author topjohncian
 */
@Serializable
sealed class RemoteBody(
    val type: String = "pack"
)


/**
 * 发送命令
 */
@Serializable
@SerialName("runcmdrequest")
class RunCmdRequestBody(
    val params: RunCmdRequestParam,
) : RemoteBody()

/**
 * 发送命令的参数
 * @param cmd 命令(不需要斜杠)
 * @param id 执行命令的唯一ID
 */
@Serializable
class RunCmdRequestParam(
    val cmd: String,
    val id: Int
)


/**
 * 发送全服信息
 */
@Serializable
@SerialName("broadcast")
class BroadcastBody(
    val params: BroadcastParam
) : RemoteBody()

/**
 * 发送全服信息的参数
 * @param text 信息
 */
@Serializable
class BroadcastParam(
    val text: String
)