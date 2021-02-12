package com.poicraft.bot.v4.plugin.remote.bdxws.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val resJson = Json {
    ignoreUnknownKeys = true
    classDiscriminator = "cause"
    encodeDefaults = true
}

/**
 * 所有 Remote Response 的基类
 * https://github.com/WangYneos/BDXWebSocket
 * @author topjohncian
 */
@Serializable
sealed class RemoteResponse

/**
 * 玩家加入
 */
@Serializable
@SerialName("join")
class OnJoinRes(
    val params: OnJoinParam
) : RemoteResponse()

/**
 * 玩家加入的参数
 * @param sender 玩家 XboxID
 * @param xuid xuid
 * @param ip 玩家 IP
 */
@Serializable
class OnJoinParam(
    val sender: String,
    val xuid: String,
    val ip: String
)


/**
 * 玩家离开
 */
@Serializable
@SerialName("left")
class OnLeftRes(
    val params: OnLeftParam
) : RemoteResponse()

/**
 * 玩家离开的参数
 * @param sender 玩家 XboxID
 * @param xuid xuid
 * @param ip 玩家 IP
 */
@Serializable
class OnLeftParam(
    val sender: String,
    val xuid: String,
    val ip: String,
)


/**
 * 玩家使用命令
 */
@Serializable
@SerialName("cmd")
class OnCmdRes(
    val params: OnCmdParam
) : RemoteResponse()

/**
 * 玩家使用命令的参数
 * @param sender 玩家 XboxID
 * @param cmd 玩家使用的命令 (带有斜杠)
 */
@Serializable
class OnCmdParam(
    val sender: String,
    val cmd: String,
)


/**
 * 玩家聊天信息
 */
@Serializable
@SerialName("chat")
class OnChatRes(
    val params: OnChatParam
) : RemoteResponse()

/**
 * 玩家聊天信息的参数
 * @param sender 玩家 XboxID
 * @param text 信息
 */
@Serializable
class OnChatParam(
    val sender: String,
    val text: String,
)


/**
 * 发送命令后服务端返回
 */
@Serializable
@SerialName("runcmdfeedback")
class OnRunCmdFeedbackRes(
    val params: OnRunCmdFeedbackParam
) : RemoteResponse()

/**
 * 发送命令后服务端返回的参数
 * @param id 命令 ID
 * @param result 命令执行结果
 */
@Serializable
class OnRunCmdFeedbackParam(
    val id: Int,
    val result: String,
)


/**
 * 密钥不匹配
 * 密匙不匹配无法解密
 */
@Serializable
@SerialName("authfailed")
class OnAuthFailedRes(
    val params: OnAuthFailedParam
) : RemoteResponse()

@Serializable
class OnAuthFailedParam(
//    密匙不匹配，无法解密数据包
    val msg: String
)


/**
 * 玩家死亡
 * **注：文档没有更新 所以参数说明缺失**
 */
@Serializable
@SerialName("die")
class OnDieRes(
    val params: OnDieParam
) : RemoteResponse()

/**
 * 玩家死亡的参数
 * **注：文档没有更新 所以参数说明缺失**
 * @param target 玩家 XBoxID
 * @param source 未知 (例:unknow)
 * @param causecode 未知 (例:8)
 * @param cause_name 未知 (例:lava)
 */
@Serializable
class OnDieParam(
    val target: String,
    val source: String,
    val causecode: String,
    val cause_name: String,
)