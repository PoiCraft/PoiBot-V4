package com.poicraft.bot.v4.plugin.commands

import com.poicraft.bot.v4.plugin.Command
import com.poicraft.bot.v4.plugin.utils.Hex2Byte
import net.mamoe.mirai.event.events.GroupMessageEvent
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

@Suppress("BlockingMethodInNonBlockingContext", "SpellCheckingInspection")
object BedrockTools : Command() {
    override val name: String = "MCBE工具"
    override val aliases: List<String> = listOf(
        "be"
    )
    override val enableSubCommand: Boolean = true

    object GetServerInfo : Command() {
        override val name: String = "获取服务器信息"
        override val aliases: List<String> = listOf(
            "info"
        )
        override val argsRequired: Int = 1
        override suspend fun handleMessage(event: GroupMessageEvent, args: List<String>) {
            val address: String
            val port: Int
            if (args[1].contains(":")) {
                val d = args[1].split(";")
                address = d[0]
                port = d[1].toInt()
            } else {
                address = args[1]
                port = 19132
            }

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

            val result = ServerInfoResult(res[1], res[3], res[4].toInt(), res[5].toInt(), res[7], res[8])

            event.subject.sendMessage(
                """服务器名称: ${result.name}
                    |游戏版本: ${result.version}
                    |地图名称: ${result.level}
                    |在线数量: ${result.player}/${result.max_player}
                """.trimMargin()
            )


        }
    }

    data class ServerInfoResult(
        var name: String,
        var version: String,
        var player: Int,
        var max_player: Int,
        var level: String,
        var game_mode: String
    )

    init {
        newSubCommand(GetServerInfo)
    }

}