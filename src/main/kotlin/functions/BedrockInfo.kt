@file:Suppress("SpellCheckingInspection")

package com.poicraft.bot.v4.plugin.functions

import com.poicraft.bot.v4.plugin.utils.Hex2Byte
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

fun getServerInfo(address: String, port: Int = 19132): ServerInfoResult {
    val sData =
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
        server_version = res[2].toInt(),
        version = res[3],
        player = res[4].toInt(),
        max_player = res[5].toInt(),
        level = res[7],
        game_mode = res[8]
    )
}

data class ServerInfoResult(
    val name: String,
    val version: String,
    val server_version: Int,
    val player: Int,
    val max_player: Int,
    val level: String,
    val game_mode: String
)