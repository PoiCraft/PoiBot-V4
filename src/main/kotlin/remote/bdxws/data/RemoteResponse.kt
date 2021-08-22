@file:Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")

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
 */
@Serializable
class OnLeftParam(
    val sender: String,
    val xuid: String
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
 * 生物死亡
 * **仅限有名字（NameTag）的生物(玩家)**
 */
@Serializable
@SerialName("mobdie")
class OnMobdieRes(
    val params: OnMobdieParam
) : RemoteResponse()

/**
 * 生物死亡的参数
 * @param mobtype 实体类型
 * @param mobname 实体名称
 * @param dmcase 伤害源ID
 * @param dmname 伤害类型
 * @param srctype 伤害源类型
 * @param srcname 伤害源名称
 * @param pos 坐标
 * @see "https://github.com/WangYneos/BDXWebSocket#mob-die"
 */
@Serializable
class OnMobdieParam(
    val mobtype: String,
    val mobname: String,
    val srctype: String,
    val srcname: String,
    val dmcase: Int,
    val dmname: String,
    val pos: Position
)

/**
 * 坐标
 */
@Serializable
class Position(
    val x: Float,
    val y: Float,
    val z: Float
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
