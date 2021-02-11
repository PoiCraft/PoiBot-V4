package com.poicraft.bot.v4.plugin.utils

object Hex2Byte {

    fun hexToByte(inHex: String): Byte {
        return inHex.toInt(16).toByte()
    }

    fun hexToByteArray(iHex: String): ByteArray {
        var inHex = iHex
        var hexlen = inHex.length
        val result: ByteArray
        if (hexlen % 2 == 1) {
            hexlen++
            result = ByteArray(hexlen / 2)
            inHex = "0$inHex"
        } else {
            result = ByteArray(hexlen / 2)
        }
        var j = 0
        var i = 0
        while (i < hexlen) {
            result[j] = hexToByte(inHex.substring(i, i + 2))
            j++
            i += 2
        }
        return result
    }
}