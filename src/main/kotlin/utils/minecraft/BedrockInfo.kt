@file:Suppress("SpellCheckingInspection")

package com.poicraft.bot.v4.plugin.utils.minecraft

import com.poicraft.bot.v4.plugin.utils.Hex2Byte
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * 获取服务器信息
 * @param address 服务器地址, 默认值为 `"127.0.0.1"`
 * @param port 服务器端口, 默认值为 `19132`
 */
fun getServerInfo(address: String = "127.0.0.1", port: Int = 19132): ServerInfoResult {
    val sData = // 抓包获得的数据
        Hex2Byte.hexToByteArray("01000000000000176B00FFFF00FEFEFEFEFDFDFDFD12345678ADDE22239AC7BD0F")

    val inetAddress = InetAddress.getByName(address)
    val sDp = DatagramPacket(sData, sData.size, inetAddress, port)
    val ds = DatagramSocket()
    ds.send(sDp)
    val rData = ByteArray(1024)
    val rDp = DatagramPacket(rData, rData.size)
    ds.receive(rDp)

    val resD = String(rDp.data, charset("utf8"))

    val res = resD.split(";").toTypedArray()
    ds.close()

    return ServerInfoResult(
        name = res[1],
        serverVersion = res[2].toInt(),
        version = res[3],
        player = res[4].toInt(),
        maxPlayer = res[5].toInt(),
        level = res[7],
        gameMode = res[8]
    )
}

data class ServerInfoResult(
    /**
     * 服务器名称
     */
    val name: String,
    /**
     * 服务器版本
     */
    val version: String,
    /**
     * 服务器协议版本
     */
    val serverVersion: Int,
    /**
     * 服务器在线玩家数量
     */
    val player: Int,
    /**
     * 服务器允许的最大玩家数量
     */
    val maxPlayer: Int,
    /**
     * 地图名称
     */
    val level: String,
    /**
     * 游戏模式
     */
    val gameMode: String
)