package com.poicraft.bot.v4.plugin.utils

object Hex2Byte {

    private fun hexToByte(inHex: String): Byte {
        return inHex.toInt(16).toByte()
    }

    fun hexToByteArray(iHex: String): ByteArray {
        var inHex = iHex
        var hexLen = inHex.length
        val result: ByteArray
        if (hexLen % 2 == 1) {
            hexLen++
            result = ByteArray(hexLen / 2)
            inHex = "0$inHex"
        } else {
            result = ByteArray(hexLen / 2)
        }
        var j = 0
        var i = 0
        while (i < hexLen) {
            result[j] = hexToByte(inHex.substring(i, i + 2))
            j++
            i += 2
        }
        return result
    }
}